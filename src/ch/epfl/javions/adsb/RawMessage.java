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
 * @param timeStampNs the time stamp of the message, expressed in nanoseconds
 *                    from the time of the very first calculated power sample,
 * @param bytes       the bytes of the message.
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public record RawMessage(long timeStampNs, ByteString bytes) {
    /**
     * LENGTH : constant of value 14
     */
    public static final int LENGTH = 14;
    private static final int PAYLOAD_START = 4;
    private static final int PAYLOAD_LENGTH = 10;
    private static final int EXPECTED_DF = 17;
    private static final int DF_START = 3;
    private static final int DF_LENGTH = 5;
    private static final int TYPECODE_START = 51;
    private static final int TYPECODE_LENGTH = 5;
    private static final int ICAO_START = 1;
    private static final int ICAO_LENGTH = 4;
    private static final int ICAO_SIZE = 6;
    private final static HexFormat hf = HexFormat.of().withUpperCase();
    private final static Crc24 crc24 = new Crc24(Crc24.GENERATOR);

    /**
     * RawMessage compact constructor
     *
     * @param timeStampNs the time stamp in nanoseconds
     * @param bytes       a byte string of length 14
     * @throws IllegalArgumentException if the timestamp is (strictly) negative, or if the byte string does not
     *                                  contain LENGTH (14) bytes
     */
    public RawMessage {
        Preconditions.checkArgument((timeStampNs >= 0) && (bytes.size() == LENGTH));
    }

    /**
     * @param timeStampNs the time stamp in nanoseconds
     * @param bytes       the bytes used to build the raw ADS-B message
     * @return the raw ADS-B message with timestamp and given bytes or null if the CRC24 of the bytes is not 0.
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        return (crc24.crc(bytes) != 0) ? null : new RawMessage(timeStampNs, new ByteString(bytes));
    }

    /**
     * @param byte0
     * @return the size of a message whose first byte is the given
     */
    public static int size(byte byte0) {
        int DF = Bits.extractUInt(byte0, DF_START, DF_LENGTH);

        return (DF == EXPECTED_DF) ? LENGTH : 0;
    }

    /**
     * @param payload
     * @return the type code of the ME attribute passed as an argument.
     */

    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, TYPECODE_START, TYPECODE_LENGTH);
    }

    /**
     * @return the DF attribute stored in its first byte
     */
    public int downLinkFormat() {
        byte byte0 = (byte) this.bytes.byteAt(0);

        return Bits.extractUInt(byte0, DF_START, DF_LENGTH);
    }

    /**
     * @return the ICAO address of the sender of the message
     */
    public IcaoAddress icaoAddress() {
        long address = this.bytes.bytesInRange(ICAO_START, ICAO_LENGTH);

        return new IcaoAddress(hf.toHexDigits(address, ICAO_SIZE));
    }

    /**
     * @return the ME attribute of the message
     */
    public long payload() {
        return this.bytes.bytesInRange(PAYLOAD_START, PAYLOAD_LENGTH + 1);
    }

    /**
     * @return the five most significant bits of its ME attribute.
     */
    public int typeCode() {
        return typeCode(this.payload());
    }
}
