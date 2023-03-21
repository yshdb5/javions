package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading)
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

        if ((ST == 1) || (ST == 2))
        {
            int directionEW = Bits.extractUInt(rawMessage.payload(), 42, 1);
            int speedEW = Bits.extractUInt(rawMessage.payload(), 32, 10);
            int directionNS = Bits.extractUInt(rawMessage.payload(), 31, 1);
            int speedNS = Bits.extractUInt(rawMessage.payload(), 21, 10);
        }



        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), 1, 1);
    }
}
