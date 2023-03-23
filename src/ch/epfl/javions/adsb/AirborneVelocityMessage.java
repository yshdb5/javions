package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) implements Message
{
    public AirborneVelocityMessage
    {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument((timeStampNs >= 0) && (speed >= 0) && (trackOrHeading >= 0));
    }


    public static AirborneVelocityMessage of(RawMessage rawMessage)
    {
        int ST = Bits.extractUInt(rawMessage.payload(), 48, 3);

        if ((ST < 1) || (ST > 4))
        {
            return null;
        }
        double speedAngle_RADIAN;
        double speedNorm_METER_PER_SECOND;

        if ((ST == 1) || (ST == 2))
        {
            int directionEW = Bits.extractUInt(rawMessage.payload(), 42, 1);
            int speedEW = Bits.extractUInt(rawMessage.payload(), 32, 10) - 1;
            int directionNS = Bits.extractUInt(rawMessage.payload(), 31, 1);
            int speedNS = Bits.extractUInt(rawMessage.payload(), 21, 10) - 1;

            if ((speedEW == -1) || (speedNS == -1))
            {
                return null;
            }

            if (directionNS == 0 && directionEW == 0)
            {
                speedAngle_RADIAN = -(Math.atan2(-speedNS, -speedEW) - Math.PI);
            }
            else if (directionNS == 1 && directionEW == 0)
            {
                speedAngle_RADIAN = Math.atan2(speedNS, -speedEW);
            }
            else if (directionNS == 0 && directionEW == 1)
            {
                speedAngle_RADIAN = -(Math.atan2(-speedNS, speedEW) - Math.PI);
            }
            else
            {
                speedAngle_RADIAN = Math.atan2(speedNS, speedEW);
            }

            if (ST == 1)
            {
                speedNorm_METER_PER_SECOND = Units.convertFrom(Math.hypot((speedNS), (speedEW)), Units.Speed.KNOT);
            }
            else
            {
                speedNorm_METER_PER_SECOND = Units.convertFrom(Math.hypot((speedNS), (speedEW))*4, Units.Speed.KNOT);
            }
            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedNorm_METER_PER_SECOND, speedAngle_RADIAN);
        }

        else
        {
            int SH = Bits.extractUInt(rawMessage.payload(), 42, 1);

            if (SH == 0)
            {
                return null;
            }
            else
            {
                speedAngle_RADIAN = Units.convertFrom(Bits.extractUInt(rawMessage.payload(), 32, 10)*Math.scalb(1, -10), Units.Angle.TURN);

                if (ST == 3)
                {
                    speedNorm_METER_PER_SECOND = Units.convertFrom(Bits.extractUInt(rawMessage.payload(), 32, 10), Units.Speed.KNOT);
                }
                else
                {
                    speedNorm_METER_PER_SECOND = Units.convertFrom(Bits.extractUInt(rawMessage.payload(), 32, 10)*4, Units.Speed.KNOT);
                }
                return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedNorm_METER_PER_SECOND, speedAngle_RADIAN);
            }
        }
    }
}
