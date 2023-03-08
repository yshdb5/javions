package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

public record RawMessage(long timeStampNs, ByteString bytes)
{
    public static final int LENGTH = 14;
    private final static HexFormat hf = HexFormat.of().withUpperCase();
    public RawMessage
    {
        Preconditions.checkArgument((timeStampNs >= 0) && (bytes.size() == LENGTH));
    }

    static RawMessage of(long timeStampNs, byte[] bytes)
    {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);

        if(crc24.crc(bytes) != 0)
        {
            return null;
        }
        else
        {
            return new RawMessage(timeStampNs, new ByteString(bytes));
        }
    }

    static int size(byte byte0)
    {
        int DF = Bits.extractUInt(byte0, 3, 5);
        if (DF == 17)
        {
            return LENGTH;
        }
        else
        {
            return 0;
        }
    }

    static  int typeCode(long payload)
    {
        return Bits.extractUInt(payload,52,5);
    }

    public int downLinkFormat()
    {
        byte byte0 = (byte) this.bytes.byteAt(0);

        return Bits.extractUInt(byte0, 3, 5);
    }

    public IcaoAddress icaoAddress()
    {
        long adress = this.bytes.bytesInRange(1, 3);

        return new IcaoAddress(hf.toHexDigits(adress));
    }

    public long payload()
    {
        return this.bytes.bytesInRange(4, 10);
    }

    public  int typeCode()
    {
        return  Bits.extractUInt(this.payload(),52,5);
    }
}
