package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message
{
    public AirbornePositionMessage
    {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument((timeStampNs >= 0) && ((parity == 0) || (parity == 1)) && ((x >= 0) && (x < 1)) && ((y >= 0) && (y < 1)));
    }
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    public static AirbornePositionMessage of(RawMessage rawMessage)
    {
        long timeStamp = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int ALT = Bits.extractUInt(rawMessage.payload(), 36, 12);
        int FORMAT = Bits.extractUInt(rawMessage.payload(), 34, 1);
        int LAT_CPR = Bits.extractUInt(rawMessage.payload(), 17, 17);
        int LON_CPR = Bits.extractUInt(rawMessage.payload(), 0, 17);

        int Q = Bits.extractUInt(ALT, 4, 1);

        if (Q == 1)
        {
            int part1 = Bits.extractUInt(ALT, 5, 7);
            int part2 = Bits.extractUInt(ALT, 0, 4);
            ALT = (part1 << 4) | part2;

            double ALT_METER = Units.convertFrom(-1000 + ALT*25, Units.Length.FOOT) ;
        }

        return new AirbornePositionMessage(timeStamp, icaoAddress,1 , FORMAT, 1, 1);
    }
}
