package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * record AirborneVelocityMessage : represents a speed message in flight of the type
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {
    private final static int SUBTYPE_START = 48;
    private final static int SUBTYPE_LENGTH = 3;
    private static final int SUBTYPE_MIN_VALUE = 1;
    private static final int SUBTYPE_MAX_VALUE = 4;
    private final static int SUBPAYLOAD_START = 21;
    private final static int SUBPAYLOAD_LENGTH = 22;
    private final static int NS_SPEED_START = 0;
    private final static int EW_SPEED_START = 11;
    private final static int SPEEDS_LENGTH = 10;
    private final static int EW_DIR_START = 21;
    private final static int NS_DIR_START = 10;
    private final static int DIR_LENGTH = 1;
    private final static int HS_START = EW_DIR_START;
    private final static int HS_LENGTH = DIR_LENGTH;
    private final static int TRACK_START = EW_SPEED_START;
    private final static int TRACK_LENGTH = SPEEDS_LENGTH;
    private final static int AIRSPEED_START = NS_SPEED_START;
    private static final double MIN_VALID_VALUE = 0;


    /**
     * AirborneVelocityMessage's constructor
     *
     * @param timeStampNs    the time stamp of the message, in nanoseconds
     * @param icaoAddress    the ICAO address of the sender of the message
     * @param speed          the speed of the aircraft, in m/s
     * @param trackOrHeading the direction of movement of the aircraft, in radians
     * @throws NullPointerException     if icaoAddress is null
     * @throws IllegalArgumentException if timeStampNs, speed or trackOrHeading are strictly negative
     */
    public AirborneVelocityMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument((timeStampNs >= MIN_VALID_VALUE) && (speed >= MIN_VALID_VALUE) && (trackOrHeading >= MIN_VALID_VALUE));
    }


    /**
     * @param rawMessage the airspeed message corresponding to the given raw message,or null if the
     *                   subtype is invalid, or if the speed or direction of travel cannot be determined.
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        int subType = Bits.extractUInt(rawMessage.payload(), SUBTYPE_START, SUBTYPE_LENGTH);
        int subPayload = Bits.extractUInt(rawMessage.payload(), SUBPAYLOAD_START, SUBPAYLOAD_LENGTH);

        if ((subType < SUBTYPE_MIN_VALUE) || (subType > SUBTYPE_MAX_VALUE)) {
            return null;
        }

        double track0rHeadingRadian;
        double speedNormMeterPerSecond;

        if ((subType == 1) || (subType == 2)) {
            int directionEW = Bits.extractUInt(subPayload, EW_DIR_START, DIR_LENGTH);
            int speedEW = Bits.extractUInt(subPayload, EW_SPEED_START, SPEEDS_LENGTH) - 1;
            int directionNS = Bits.extractUInt(subPayload, NS_DIR_START, DIR_LENGTH);
            int speedNS = Bits.extractUInt(subPayload, NS_SPEED_START, SPEEDS_LENGTH) - 1;

            if (isInvalidSpeed(speedEW) || isInvalidSpeed(speedNS)) return null;

            track0rHeadingRadian = calculateTrackHeading(directionNS, directionEW, speedEW, speedNS);

            if (track0rHeadingRadian < 0) {
                track0rHeadingRadian += 2 * Math.PI;
            }

            speedNormMeterPerSecond = calculateSpeedNormMeterPerSecond(subType, speedNS, speedEW);

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedNormMeterPerSecond, track0rHeadingRadian);
        } else {
            int capAvailability = Bits.extractUInt(subPayload, HS_START, HS_LENGTH);

            if (capAvailability == 1) {
                track0rHeadingRadian = calculateTrackHeading(subPayload);

                int temporarySpeed = (Bits.extractUInt(subPayload, AIRSPEED_START, SPEEDS_LENGTH) - 1);

                if (isInvalidSpeed(temporarySpeed)) return null;

                speedNormMeterPerSecond = (subType == 3) ? Units.convertFrom(temporarySpeed, Units.Speed.KNOT) :
                        Units.convertFrom(temporarySpeed * 4, Units.Speed.KNOT);

                return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedNormMeterPerSecond, track0rHeadingRadian);
            }
            return null;
        }
    }

    private static double calculateTrackHeading(int directionNS, int directionEW, int speedEW, int speedNS) {
        if (directionNS == 0 && directionEW == 0) {
            return Math.atan2(speedEW, speedNS);
        } else if (directionNS == 1 && directionEW == 0) {
            return Math.atan2(speedEW, -speedNS);
        } else if (directionNS == 0 && directionEW == 1) {
            return Math.atan2(-speedEW, speedNS);
        } else {
            return Math.atan2(-speedEW, -speedNS);
        }
    }

    private static double calculateTrackHeading(int subPayload) {
        return Units.convertFrom(Bits.extractUInt(subPayload, TRACK_START, TRACK_LENGTH) * Math.scalb(1d, -10), Units.Angle.TURN);
    }

    private static double calculateSpeedNormMeterPerSecond(int subType, double speedNS, double speedEW) {
        double hypot = Math.hypot(speedNS, speedEW);
        double speedNormKnots = (subType == 1) ? hypot : hypot * 4;
        return Units.convertFrom(speedNormKnots, Units.Speed.KNOT);
    }

    private static boolean isInvalidSpeed(int speed) {
        return speed == -1;
    }
}
