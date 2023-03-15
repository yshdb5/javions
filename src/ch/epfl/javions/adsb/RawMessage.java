package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

/**
 * record RawMessage : represents an ADS-B message whose ME attribute has not yet been analyzed
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 * @param timeStampNs
 *        the time stamp of the message, expressed in nanoseconds
 *        from the time of the very first calculated power sample,
 * @param bytes
 *        the bytes of the message.
 */

public record RawMessage(long timeStampNs, ByteString bytes)
{
    /**
     * LENGTH : constant of value 14
     */
    public static final int LENGTH = 14;
    private final static HexFormat hf = HexFormat.of().withUpperCase();

    /**
     * Rawmessage compact constructor
     * @param timeStampNs
     * @param bytes
     * @throws IllegalArgumentException if the timestamp is (strictly) negative, or if the byte string does not
     *                                  contain LENGTH (14) bytes
     */
    public RawMessage
    {
        Preconditions.checkArgument((timeStampNs >= 0) && (bytes.size() == LENGTH));
    }

    /**
     * @param timeStampNs
     * @param bytes
     * @return the raw ADS-B message with timestamp and given bytes or null if the CRC24 of the bytes is not 0.
     */
    public static RawMessage of(long timeStampNs, byte[] bytes)
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

    /**
     * @param byte0
     * @return the size of a message whose first byte is the given
     */
    public static int size(byte byte0)
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

    /**
     * @param payload
     * @return the type code of the ME attribute passed as an argument.
     */

    public static  int typeCode(long payload)
    {
        return Bits.extractUInt(payload,51,5);
    }

    /**
     * @return the DF attribute stored in its first byte
     */
    public int downLinkFormat()
    {
        byte byte0 = (byte) this.bytes.byteAt(0);

        return Bits.extractUInt(byte0, 3, 5);
    }

    /**
     * @return the ICAO address of the sender of the message
     */
    public IcaoAddress icaoAddress()
    {
        long adress = this.bytes.bytesInRange(1, 4);

        return new IcaoAddress(hf.toHexDigits(adress, 6));
    }

    /**
     * @return the ME attribute of the message
     */
    public long payload()
    {
        return this.bytes.bytesInRange(4, 11);
    }

    /**
     * @return the five most significant bits of its ME attribute.
     */
    public  int typeCode()
    {
        return typeCode(this.payload());
    }
}
