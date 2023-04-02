package ch.epfl.javions.demodulation;

import ch.epfl.javions.Bits;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * final class AdsbDemodulator : represents a demodulator for ADS-B messages
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class AdsbDemodulator {
    private static final int WINDOWSIZE = 1200;
    private static final int INDEXPICS2 = 10;
    private static final int INDEXPICS3 = 35;
    private static final int INDEXPICS4 = 45;
    private static final int INDEXVALLEYS1 = 5;
    private static final int INDEXVALLEYS2 = 15;
    private static final int INDEXVALLEYS3 = 20;
    private static final int INDEXVALLEYS4 = 25;
    private static final int INDEXVALLEYS5 = 30;
    private static final int INDEXVALLEYS6 = 40;
    private static final int EXPECTED_DF = 17;
    private static final int PREAMBLE_SIZE = 80;
    private static final int PERIOD = 5;
    private final PowerWindow powerWindow;


    /**
     * returns a demodulator obtaining the bytes containing the samples of the stream passed in argument
     *
     * @param samplesStream the samples of the stream that are used to build a demodulator
     * @throws IOException if an input/output error occurs when creating the PowerWindow object
     *                     representing the 1200 power sample window, used to search for messages.
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, WINDOWSIZE);
    }

    /**
     * @return the next ADS-B message in the sample stream passed to the constructor,
     * or null if the end of the sample stream has been reached
     * @throws IOException in case of an input/output error.
     */
    public RawMessage nextMessage() throws IOException {
        int pics0 = 0;
        int picsPlus1;
        int picsMinus1;

        while (powerWindow.isFull()) {
            picsMinus1 = pics0;
            pics0 = sumPics(0);
            picsPlus1 = sumPics(1);

            if (isPreamble(picsMinus1, pics0, picsPlus1)) {
                byte[] message = new byte[RawMessage.LENGTH];

                message[0] = byteI(0);

                if (!dfIsOk(message[0])) {
                    powerWindow.advance();
                    continue;
                }

                for (int i = 1; i < RawMessage.LENGTH; i++) {
                    message[i] = byteI(i);
                }

                long timeStamp = powerWindow.position() * 100;
                RawMessage maybeMessage = RawMessage.of(timeStamp, message);

                if (maybeMessage != null) {
                    powerWindow.advanceBy(WINDOWSIZE);
                    return maybeMessage;
                } else {
                    powerWindow.advance();
                }
            } else {
                powerWindow.advance();
            }
        }

        return null;
    }

    private boolean isPreamble(int picsMinus1, int pics0, int picsPlus1) {
        boolean condition1 = pics0 >= 2 * sumValley();
        boolean condition2 = picsMinus1 < pics0;
        boolean condition3 = pics0 > picsPlus1;

        return condition1 && condition2 && condition3;
    }

    private int sumPics(int i) {
        return powerWindow.get(i) + powerWindow.get(INDEXPICS2 + i) + powerWindow.get(INDEXPICS3 + i) + powerWindow.get(INDEXPICS4 + i);
    }

    private int sumValley() {
        return powerWindow.get(INDEXVALLEYS1) + powerWindow.get(INDEXVALLEYS2) + powerWindow.get(INDEXVALLEYS3) + powerWindow.get(INDEXVALLEYS4) + powerWindow.get(INDEXVALLEYS5) + powerWindow.get(INDEXVALLEYS6);
    }

    private byte bitI(int i) {
        if (powerWindow.get(PREAMBLE_SIZE + (2 * PERIOD) * i) < powerWindow.get((PREAMBLE_SIZE + PERIOD) + (2 * PERIOD) * i)) {
            return 0;
        } else {
            return 1;
        }
    }

    private byte byteI(int j) {
        byte b = 0;
        for (int i = j * Byte.SIZE, h = 0; i < j * Byte.SIZE + Byte.SIZE; i++, h++) {
            b = (byte) (b | (bitI(i) << (7 - h)));
        }

        return b;
    }

    private boolean dfIsOk(byte byte0) {
        int DF = Bits.extractUInt(byte0, 3, 5);
        return DF == EXPECTED_DF;
    }
}
