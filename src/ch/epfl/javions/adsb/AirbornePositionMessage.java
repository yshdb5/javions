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
        double ALT_METER;
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

            ALT_METER = Units.convertFrom(-1000 + ALT*25, Units.Length.FOOT);
        }
        else
        {
            int disentangledAlt = disentangling(ALT);

            int part1 = Bits.extractUInt(disentangledAlt, 0, 3);
            int part2 = Bits.extractUInt(disentangledAlt, 3, 9);

            part1 = grayCodeValueOf(part1, 3);
            part2 = grayCodeValueOf(part2, 9);

            if (part1 == 0 || part1 == 5 || part1 == 6)
            {
                return null;
            }
            else if (part1 == 7)
            {
                part1 = 5;
            }
            else if (part2 % 2 == 1)
            {
               part1 = 6 - part1;
            }

            ALT_METER = Units.convertFrom(-1300 + part1*100 + part2*500, Units.Length.FOOT);
        }

        return new AirbornePositionMessage(timeStamp, icaoAddress, ALT_METER , FORMAT, 1, 1);
    }

    private static int disentangling(int ALT)
    {
        int D1 = Bits.extractUInt(ALT, 4, 1);
        int D2 = Bits.extractUInt(ALT, 2, 1);
        int D4 = Bits.extractUInt(ALT, 0, 1);
        int A1 = Bits.extractUInt(ALT, 10, 1);
        int A2 = Bits.extractUInt(ALT, 8, 1);
        int A4 = Bits.extractUInt(ALT, 6, 1);
        int B1 = Bits.extractUInt(ALT, 5, 1);
        int B2 = Bits.extractUInt(ALT, 3, 1);
        int B4 = Bits.extractUInt(ALT, 1, 1);
        int C1 = Bits.extractUInt(ALT, 11, 1);
        int C2 = Bits.extractUInt(ALT, 9, 1);
        int C4 = Bits.extractUInt(ALT, 7, 1);

        return ((((((((((((D1 << 11) | D2 << 10) | D4 << 9) | A1 << 8) | A2 << 7) | A4 << 6) | B1 << 5) | B2 << 4) | B4 << 3) | C1 << 2) | C2 << 1) | C4);
    }

    private static int grayCodeValueOf(int value, int length)
    {
        int grayCodeValue = 0;

        for (int i = 0; i < length; i++)
        {
            grayCodeValue = grayCodeValue ^ (value >> i);
        }

        return grayCodeValue;
    }
}
