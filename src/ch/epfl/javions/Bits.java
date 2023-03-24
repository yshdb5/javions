package ch.epfl.javions;
/**
 * class Bits : contains methods to extract a subset of the 64 bits of a long type value
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */


import java.util.Objects;

public class Bits {
    private Bits() {
    }

    /**
     * extracts UInt
     * extracts from the 64-bit value vector the size-bit range
     * starting at the start index bit which it interprets as an unsigned value
     *
     * @param value the 64 - bit value
     * @param start the start index
     * @param size  the size needed to be extracted
     * @return the extracted value as an integer
     * @throws IllegalArgumentException if the size is negative or bigger than the size of an integer
     */
    public static int extractUInt(long value, int start, int size) {
        Preconditions.checkArgument((size > 0) && (size < Integer.SIZE));
        Objects.checkFromIndexSize(start, size, Long.SIZE);

        long mask = (1L << size) - 1;
        long extractedValue = (value >>> start) & mask;
        return (int) extractedValue;
    }

    /**
     * tests if the bit value is 1 on a long.
     *
     * @param value the long value we want to test
     * @param index the index we want to test
     * @return true if the value of the index given is 1
     */
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);

        long mask = 1L << index;
        return (value & mask) != 0;
    }
}
