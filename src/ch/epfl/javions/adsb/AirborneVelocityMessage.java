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
        Preconditions.checkArgument((timeStampNs >= 0) && (speed >= 0) && (trackOrHeading >= 0));
    }


    /**
     * @param rawMessage the airspeed message corresponding to the given raw message,or null if the
     *                   subtype is invalid, or if the speed or direction of travel cannot be determined.
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        int subType = Bits.extractUInt(rawMessage.payload(), 48, 3);

        if ((subType < 1) || (subType > 4)) {
            return null;
        }
        double trackOrHeading_RADIAN;
        double speedNorm_METER_PER_SECOND;

        if ((subType == 1) || (subType == 2)) {
            int directionEW = Bits.extractUInt(rawMessage.payload(), 42, 1);
            int speedEW = Bits.extractUInt(rawMessage.payload(), 32, 10) - 1;
            int directionNS = Bits.extractUInt(rawMessage.payload(), 31, 1);
            int speedNS = Bits.extractUInt(rawMessage.payload(), 21, 10) - 1;

            if ((speedEW == -1) || (speedNS == -1)) {
                return null;
            }

            if (directionNS == 0 && directionEW == 0) {
                trackOrHeading_RADIAN = Math.atan2(speedEW, speedNS);
            } else if (directionNS == 1 && directionEW == 0) {
                trackOrHeading_RADIAN = Math.atan2(speedEW, -speedNS);
            } else if (directionNS == 0 && directionEW == 1) {
                trackOrHeading_RADIAN = Math.atan2(-speedEW, speedNS);
            } else {
                trackOrHeading_RADIAN = Math.atan2(-speedEW, -speedNS);
            }

            if (trackOrHeading_RADIAN < 0) {
                trackOrHeading_RADIAN += 2 * Math.PI;
            }

            if (subType == 1) {
                speedNorm_METER_PER_SECOND = Units.convertFrom(Math.hypot(speedNS, speedEW), Units.Speed.KNOT);
            } else {
                speedNorm_METER_PER_SECOND = Units.convertFrom(Math.hypot(speedNS, speedEW) * 4, Units.Speed.KNOT);
            }
            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedNorm_METER_PER_SECOND, trackOrHeading_RADIAN);
        } else {
            int capAvailability = Bits.extractUInt(rawMessage.payload(), 42, 1);

            if (capAvailability == 0) {
                return null;
            } else {
                trackOrHeading_RADIAN = Units.convertFrom(Bits.extractUInt(rawMessage.payload(), 32, 10) * Math.scalb(1d, -10), Units.Angle.TURN);

                if (subType == 3) {
                    speedNorm_METER_PER_SECOND = Units.convertFrom(Bits.extractUInt(rawMessage.payload(), 32, 10), Units.Speed.KNOT);
                } else {
                    speedNorm_METER_PER_SECOND = Units.convertFrom(Bits.extractUInt(rawMessage.payload(), 32, 10) * 4, Units.Speed.KNOT);
                }
                return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedNorm_METER_PER_SECOND, trackOrHeading_RADIAN);
            }
        }
    }
}
