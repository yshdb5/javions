package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Final class AdsbDemodulator : represents a demodulator for ADS-B messages.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class AdsbDemodulator {
    public static final int TIME_FACTOR = 100;
    private static final int WINDOWSIZE = 1200;
    private static final int INDEXPEAK2 = 10;
    private static final int INDEXPEAK3 = 35;
    private static final int INDEXPEAK4 = 45;
    private static final int INDEXVALLEYS1 = 5;
    private static final int INDEXVALLEYS2 = 15;
    private static final int INDEXVALLEYS3 = 20;
    private static final int INDEXVALLEYS4 = 25;
    private static final int INDEXVALLEYS5 = 30;
    private static final int INDEXVALLEYS6 = 40;
    private static final int PREAMBLE_SIZE = 80;
    private static final int PERIOD = 5;
    private static final byte[] message = new byte[RawMessage.LENGTH];
    private final PowerWindow powerWindow;



    /**
     * return a demodulator obtaining the bytes containing the samples of the stream passed in argument
     *
     * @param samplesStream the samples of the stream that are used to build a demodulator
     * @throws IOException if an input/output error occurs when creating the PowerWindow object
     *                     representing the 1200 power sample window, used to search for messages.
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, WINDOWSIZE);
    }

    /**
     *
     * @return the next ADS-B message in the sample stream passed to the constructor,
     * or null if the end of the sample stream has been reached
     * @throws IOException in case of an input/output error.
     */
    public RawMessage nextMessage() throws IOException {
        int peak0 = 0;
        int peakPlus1;
        int peakMinus1;

        while (powerWindow.isFull()) {
            peakMinus1 = peak0;
            peak0 = sumPeaks(0);
            peakPlus1 = sumPeaks(1);

            if (isPreamble(peakMinus1, peak0, peakPlus1)) {
                message[0] = byteI(0);

                if (!dfIsOk(message[0])) {
                    powerWindow.advance();
                    continue;
                }

                for (int i = 1; i < RawMessage.LENGTH; i++) {
                    message[i] = byteI(i);
                }

                long timeStamp = powerWindow.position() * TIME_FACTOR;
                RawMessage maybeMessage = RawMessage.of(timeStamp, message);

                if (maybeMessage != null) {
                    powerWindow.advanceBy(WINDOWSIZE);
                    return maybeMessage;
                }
            }
            powerWindow.advance();
        }
        return null;
    }

    private boolean isPreamble(int peakMinus1, int peak0, int peakPlus1) {
        boolean condition1 = peak0 >= 2 * sumValleys();
        boolean condition2 = peakMinus1 < peak0;
        boolean condition3 = peak0 > peakPlus1;

        return condition1 && condition2 && condition3;
    }

    private int sumPeaks(int i) {
        return powerWindow.get(i) + powerWindow.get(INDEXPEAK2 + i) + powerWindow.get(INDEXPEAK3 + i) + powerWindow.get(INDEXPEAK4 + i);
    }

    private int sumValleys() {
        return powerWindow.get(INDEXVALLEYS1) + powerWindow.get(INDEXVALLEYS2) + powerWindow.get(INDEXVALLEYS3)
                + powerWindow.get(INDEXVALLEYS4) + powerWindow.get(INDEXVALLEYS5) + powerWindow.get(INDEXVALLEYS6);
    }

    private byte bitI(int i) {
        return (byte) (powerWindow.get(PREAMBLE_SIZE + (2 * PERIOD) * i)
                < powerWindow.get((PREAMBLE_SIZE + PERIOD) + (2 * PERIOD) * i) ? 0 : 1);
    }

    private byte byteI(int j) {
        byte b = 0;
        for (int i = j * Byte.SIZE, h = 0; i < j * Byte.SIZE + Byte.SIZE; i++, h++) {
            b |= (bitI(i) << (7 - h));
        }

        return b;
    }

    private boolean dfIsOk(byte byte0) {
        return RawMessage.size(byte0) == RawMessage.LENGTH;
    }
}
