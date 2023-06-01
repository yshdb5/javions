package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Record AirbornePositionMessage : represents an ADS-B flight position message.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x,
                                      double y) implements Message {
    private final static int ALT_START = 36;
    private final static int ALT_LENGTH = 12;
    private final static int PARITY_START = 34;
    private final static int BIT_SIZE = 1;
    private final static int LON_CPR_START = 0;
    private final static int LAT_CPR_START = 17;
    private final static int LAT_LON_LENGTH = 17;
    private final static int Q_POSITION = 4;
    private final static int Q1_PART1_START = 5;
    private final static int Q1_PART1_LENGTH = 7;
    private final static int Q1_PART2_START = 0;
    private final static int Q1_PART2_LENGTH = 4;
    private final static int Q0_PART1_START = 0;
    private final static int Q0_PART1_LENGTH = 3;
    private final static int Q0_PART2_START = 3;
    private final static int Q0_PART2_LENGTH = 9;
    private final static int A1_POS = 10;
    private final static int A2_POS = 8;
    private final static int A4_POS = 6;
    private final static int B1_POS = 5;
    private final static int B2_POS = 3;
    private final static int B4_POS = 1;
    private final static int C1_POS = 11;
    private final static int C2_POS = 9;
    private final static int C4_POS = 7;
    private final static int D1_POS = 4;
    private final static int D2_POS = 2;
    private final static int D4_POS = 0;
    private final static int BITS_NUMBER = 12;
    private final static int SHIFT_VALUE = 4;
    private final static double DIVISOR = Math.scalb(1d, -17);


    /**
     * @param timeStampNs the time stamp of the message, in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param altitude    the altitude at which the aircraft was at the time the message was sent, in meters
     * @param parity      the parity of the message (0 if it is even, 1 if it is odd)
     * @param x           the local and normalized longitude (between 0 and 1) at which the
     *                    aircraft was located when the message was sent,
     * @param y           the local and normalized latitude (between 0 and 1) at which
     *                    the aircraft was located when the message was sent
     * @throws NullPointerException     if icaoAddress is null
     * @throws IllegalArgumentException if timeStamp is strictly less than 0, or parity is different from 0 or 1,
     *                                  or x or y are not between 0 (included) and 1 (excluded).
     */
    public AirbornePositionMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(
                (timeStampNs >= 0)
                        && ((parity == 0) || (parity == 1))
                        && ((x >= 0) && (x < 1))
                        && ((y >= 0) && (y < 1)));
    }

    /**
     * @param rawMessage the raw message that gives the flight position
     * @return the flight positioning message corresponding to the given raw message
     * or null if the altitude it contains is invalid
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {

        long timeStamp = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();

        int altitude = Bits.extractUInt(rawMessage.payload(), ALT_START, ALT_LENGTH);
        int parity = Bits.extractUInt(rawMessage.payload(), PARITY_START, BIT_SIZE);

        double latitude = Bits.extractUInt(rawMessage.payload(), LAT_CPR_START, LAT_LON_LENGTH) * DIVISOR;
        double longitude = Bits.extractUInt(rawMessage.payload(), LON_CPR_START, LAT_LON_LENGTH) * DIVISOR;

        double altitudeMeter;

        if (Bits.testBit(altitude, Q_POSITION)) {
            int part1 = Bits.extractUInt(altitude, Q1_PART1_START, Q1_PART1_LENGTH);
            int part2 = Bits.extractUInt(altitude, Q1_PART2_START, Q1_PART2_LENGTH);
            altitude = (part1 << SHIFT_VALUE) | part2;

            altitudeMeter = Units.convertFrom(-1000 + altitude * 25, Units.Length.FOOT);
        } else {
            int disentangledAlt = disentangling(altitude);

            int part1 = Bits.extractUInt(disentangledAlt, Q0_PART1_START, Q0_PART1_LENGTH);
            int part2 = Bits.extractUInt(disentangledAlt, Q0_PART2_START, Q0_PART2_LENGTH);

            part1 = grayCodeValueOf(part1, Q0_PART1_LENGTH);
            part2 = grayCodeValueOf(part2, Q0_PART2_LENGTH);

            if (part1 == 0 || part1 == 5 || part1 == 6) return null;
            if (part1 == 7) part1 = 5;
            if (part2 % 2 == 1) part1 = 6 - part1;

            altitudeMeter = Units.convertFrom(-1300 + part1 * 100 + part2 * 500, Units.Length.FOOT);
        }

        return new AirbornePositionMessage(timeStamp, icaoAddress, altitudeMeter, parity, longitude, latitude);
    }

    private static int disentangling(int altitude) {

        int[] bitPositions = {D1_POS, D2_POS, D4_POS, A1_POS, A2_POS, A4_POS, B1_POS, B2_POS, B4_POS, C1_POS, C2_POS, C4_POS};
        int disentangledAlt = 0;

        for (int i = 0; i < BITS_NUMBER; i++) {
            disentangledAlt |= Bits.extractUInt(altitude, bitPositions[i], BIT_SIZE) << ((BITS_NUMBER - 1) - i);
        }

        return disentangledAlt;
    }

    private static int grayCodeValueOf(int value, int length) {
        int grayCodeValue = 0;

        for (int i = 0; i < length; i++) {
            grayCodeValue ^= value >> i;
        }

        return grayCodeValue;
    }
}
