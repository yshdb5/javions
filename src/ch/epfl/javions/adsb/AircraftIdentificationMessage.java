package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message
{
    public AircraftIdentificationMessage
    {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }
    @Override
    public long timeStampNs()
    {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress()
    {
        return icaoAddress;
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage)
    {
        int typeCode = rawMessage.typeCode();

        if (!(typeCode >= 1 && typeCode <= 4))
        {
            return null;
        }

        int CA = Bits.extractUInt(rawMessage.payload(), 48, 3);
        int C1 = Bits.extractUInt(rawMessage.payload(), 42, 6);
        int C2 = Bits.extractUInt(rawMessage.payload(), 36, 6);
        int C3 = Bits.extractUInt(rawMessage.payload(), 30, 6);
        int C4 = Bits.extractUInt(rawMessage.payload(), 24, 6);
        int C5 = Bits.extractUInt(rawMessage.payload(), 18, 6);
        int C6 = Bits.extractUInt(rawMessage.payload(), 12, 6);
        int C7 = Bits.extractUInt(rawMessage.payload(), 6, 6);
        int C8 = Bits.extractUInt(rawMessage.payload(), 0, 6);

        int [] tab = {C1, C2, C3, C4, C5, C6, C7, C8};
        String callstring = "";

        for (int i : tab)
        {
            if (!((i >= 1 && i <= 26) || (i >= 48 && i <= 57) || (i == 32)))
            {
                return null;
            }
            else
            {
                callstring += i;
            }
        }

        long timeStamps = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int category = (byte) ((14 - typeCode) << 4) | CA;

        CallSign callSign = new CallSign(callstring);

        return new AircraftIdentificationMessage(timeStamps, icaoAddress, category, callSign);
    }
}
