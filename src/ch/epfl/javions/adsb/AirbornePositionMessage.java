package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * record AirbornePositionMessage : represents an ADS-B flight position message
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x,
                                      double y) implements Message {
    private final static int ALT_START = 36;
    private final static int ALT_LENGTH = 12;
    private final static int PARITY_START = 34;
    private final static int PARITY_LENGTH = 1;
    private final static int LON_CPR_START = 0;
    private final static int LAT_CPR_START = 17;
    private final static int LAT_LON_LENGTH = LAT_CPR_START;
    private final static int Q_START = 4;
    private final static int Q_LENGTH = PARITY_LENGTH;


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

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    /**
     * @param rawMessage
     * @return the flight positioning message corresponding to the given raw message
     * or null if the altitude it contains is invalid
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {
        long timeStamp = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        double altitudeMeter;
        int altitude = Bits.extractUInt(rawMessage.payload(), ALT_START, ALT_LENGTH);
        int parity = Bits.extractUInt(rawMessage.payload(), PARITY_START, PARITY_LENGTH);
        double latitude = Bits.extractUInt(rawMessage.payload(), LAT_CPR_START, LAT_LON_LENGTH) * Math.scalb(1d, -17);
        double longitude = Bits.extractUInt(rawMessage.payload(), LON_CPR_START, LAT_LON_LENGTH) * Math.scalb(1d, -17);

        int Q = Bits.extractUInt(altitude, Q_START, Q_LENGTH);

        if (Q == 1) {
            int part1 = Bits.extractUInt(altitude, 5, 7);
            int part2 = Bits.extractUInt(altitude, 0, 4);
            altitude = (part1 << 4) | part2;

            altitudeMeter = Units.convertFrom(-1000 + altitude * 25, Units.Length.FOOT);
        } else {
            int disentangledAlt = disentangling(altitude);

            int part1 = Bits.extractUInt(disentangledAlt, 0, 3);
            int part2 = Bits.extractUInt(disentangledAlt, 3, 9);

            part1 = grayCodeValueOf(part1, 3);
            part2 = grayCodeValueOf(part2, 9);

            if (part1 == 0 || part1 == 5 || part1 == 6) {
                return null;
            }
            if (part1 == 7) {
                part1 = 5;
            }
            if (part2 % 2 == 1) {
                part1 = 6 - part1;
            }

            altitudeMeter = Units.convertFrom(-1300 + part1 * 100 + part2 * 500, Units.Length.FOOT);
        }

        return new AirbornePositionMessage(timeStamp, icaoAddress, altitudeMeter, parity, longitude, latitude);
    }

    private static int disentangling(int altitude) {

        int[] bitPositions = {4, 2, 0, 10, 8, 6, 5, 3, 1, 11, 9, 7};
        int disentangledAlt = 0;

        for (int i = 0; i < 12; i++) {
            disentangledAlt |= Bits.extractUInt(altitude, bitPositions[i], 1) << (11 - i);
        }

        return disentangledAlt;
    }

    private static int grayCodeValueOf(int value, int length) {
        int grayCodeValue = 0;

        for (int i = 0; i < length; i++) {
            grayCodeValue = grayCodeValue ^ (value >> i);
        }

        return grayCodeValue;
    }
}
