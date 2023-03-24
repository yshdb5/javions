package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * record AircraftIdentificationMessage : represents an ADS-B message of identification and category
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */


public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message
{
    /**
     *
     * @param timeStampNs
     *        the time stamp of the message, in nanoseconds
     * @param icaoAddress
     *        the ICAO address of the sender of the message
     * @param category
     *        the category of aircraft of the shipper
     * @param callSign
     *        the sender's call sign
     * @throws NullPointerException if icaoAddress or callSign are nul
     * @throws NullPointerException if timeStampNs is strictly less than 0.
     */
    public AircraftIdentificationMessage
    {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * redefinition of timeStamps
     * @return
     */
    @Override
    public long timeStampNs()
    {
        return timeStampNs;
    }

    /**
     * redefinition of icaoAdress
     * @return
     */
    @Override
    public IcaoAddress icaoAddress()
    {
        return icaoAddress;
    }

    /**
     * @param rawMessage
     * @return the identification message corresponding to the given raw message,
     * or null if at least one of the characters of the code it contains is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage)
    {
        int typeCode = rawMessage.typeCode();

        if (!(typeCode >= 1 && typeCode <= 4))
        {
            return null;
        }

        int CA = Bits.extractUInt(rawMessage.payload(), 48, 3);

        int [] tab = getTab(rawMessage.payload());

        String callstring = "";

        for (int i : tab)
        {
            if (i >= 1 && i <= 26)
            {
                callstring += (char) (i + 64);
            }
            else if ((i >= 48 && i <= 57) || (i == 32))
            {
                callstring += (char) i;
            }
            else
            {
                return null;
            }
        }

        while (callstring.endsWith(" "))
        {
            callstring = callstring.substring(0, (callstring.length() -1));
        }

        long timeStamps = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int category = Byte.toUnsignedInt((byte) (((14 - typeCode) << 4) | CA));

        CallSign callSign = new CallSign(callstring);

        return new AircraftIdentificationMessage(timeStamps, icaoAddress, category, callSign);
    }

    private static int [] getTab (long payload)
    {
        int C1 = Bits.extractUInt(payload, 42, 6);
        int C2 = Bits.extractUInt(payload, 36, 6);
        int C3 = Bits.extractUInt(payload, 30, 6);
        int C4 = Bits.extractUInt(payload, 24, 6);
        int C5 = Bits.extractUInt(payload, 18, 6);
        int C6 = Bits.extractUInt(payload, 12, 6);
        int C7 = Bits.extractUInt(payload, 6, 6);
        int C8 = Bits.extractUInt(payload, 0, 6);

        return new int [] {C1, C2, C3, C4, C5, C6, C7, C8};
    }
}
