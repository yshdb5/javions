package ch.epfl.javions;


import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Class ByteString: represents a sequence of byte.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class ByteString {
    private final static HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();
    private final static int SHIFT_VALUE = 8;
    private final byte[] bytes;

    /**
     * Constructor of ByteString, returns a string of bytes whose content is the one
     * of the array passed as argument.
     * clones bytes (immutable)
     *
     * @param bytes the bytes we want to clone
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }


    /**
     * ByteString of a Hexadecimal String.
     *
     * @param hexString the hexadecimal representation of the string
     * @return the byte string of which the string passed as argument is the hexadecimal representation
     * @throws IllegalArgumentException if the hexadecimal string length is not even
     * @throws NumberFormatException    if the sequence given is not an even size or if it contains a non-hexadecimal number
     */
    public static ByteString ofHexadecimalString(String hexString) {
        byte[] bytes = HEX_FORMAT.parseHex(hexString);

        return new ByteString(bytes);
    }

    /**
     * Size of the sequence.
     *
     * @return the number of byte the string contains
     */
    public int size() {
        return bytes.length;
    }

    /**
     * @param index the index of the byte
     * @return the byte corresponding to the index given.
     * @throws IndexOutOfBoundsException if the index given is invalid
     */
    public int byteAt(int index) {
        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * @param fromIndex the index where we want to start
     * @param toIndex   the index where we want to end
     * @return the bytes between fromIndex and toIndex-1 as a long value (the low byte value is at toIndex - 1)
     * @throws IndexOutOfBoundsException if the range between fromIndex and toIndex isn't between 0 and the size of the string.
     * @throws IllegalArgumentException  if the difference between toIndex and fromIndex isn't strictly
     *                                   lower to the number of byte in a long type value
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, this.size());
        Preconditions.checkArgument((toIndex - fromIndex) < Long.BYTES);

        long mask = Byte.toUnsignedInt(bytes[fromIndex]);

        for (int i = (fromIndex + 1); i < toIndex; i++) {
            mask = ((mask << SHIFT_VALUE) | byteAt(i));
        }

        return mask;
    }


    /**
     * redefinition of equals
     *
     * @param obj the object we want to test
     * @return true iff the value given is also an instance of ByteString and his bytes are the same as the receptor ones
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ByteString) && (Arrays.equals(((ByteString) obj).bytes, this.bytes));
    }

    /**
     * hashcode method of Arrays
     *
     * @return apply the hashCode method to the Array containing the bytes.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /**
     * redefinition of toString
     *
     * @return a representation of the bytes of the string in hexadecimal
     * each byte taking up exactly 2 characters
     */
    @Override
    public String toString() {
        return HEX_FORMAT.formatHex(bytes);
    }
}
