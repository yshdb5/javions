package ch.epfl.javions;

/**
 * final class Crc24.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class Crc24 {
    /**
     * constant
     * contains the 24 bits of lower importance of the generator used to calculate the CRC24 of ADS-B messages
     */
    public static final int GENERATOR = 0xFFF409;
    private static final int CRC_BITS = 24;
    private static final int TABLE_SIZE = 256;
    private final int[] table;

    /**
     * public constructor of Crc24
     *
     * @param generator the generator used to calculate a CRC24
     */
    public Crc24(int generator) {
        table = buildTable(generator);
    }

    private static int crc_bitwise(int generator, byte[] bytes) {
        int[] tab = {0, generator};
        int crc = 0;

        for (byte octet : bytes) {
            for (int i = 7; i >= 0; i--) {
                int b = Bits.extractUInt(octet, i, 1);

                crc = ((crc << 1) | b) ^ tab[Bits.extractUInt(crc, (CRC_BITS - 1), 1)];
            }
        }

        for (int i = 0; i < CRC_BITS; i++) {
            crc = (crc << 1) ^ tab[Bits.extractUInt(crc, (CRC_BITS - 1), 1)];
        }

        crc = Bits.extractUInt(crc, 0, CRC_BITS);

        return crc;
    }

    private static int[] buildTable(int generator) {
        int[] table = new int[TABLE_SIZE];

        for (int i = 0; i < TABLE_SIZE; i++) {
            byte[] tab = {(byte) i};
            table[i] = crc_bitwise(generator, tab);
        }

        return table;
    }

    /**
     * @param bytes the bytes use to calculate the crc
     * @return the CRC24 of the array given
     */
    public int crc(byte[] bytes) {
        int crc = 0;

        for (byte o : bytes) {
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(o)) ^ table[Bits.extractUInt(crc, (CRC_BITS - Byte.SIZE), Byte.SIZE)];
        }

        for (int i = 0; i < 3; i++) {
            crc = (crc << Byte.SIZE) ^ table[Bits.extractUInt(crc, (CRC_BITS - Byte.SIZE), Byte.SIZE)];
        }

        crc = Bits.extractUInt(crc, 0, CRC_BITS);

        return crc;
    }
}
