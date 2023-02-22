package ch.epfl.javions;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/02/23
 */

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString
{
    private byte [] bytes;
    private final static HexFormat hf = HexFormat.of().withUpperCase();
    public ByteString(byte[] bytes)
    {
        this.bytes = bytes.clone();
    }

    public static ByteString ofHexadecimalString(String hexString)
    {
        if ((hexString.length() % 2) != 0)
        {
            throw new IllegalArgumentException();
        }

        byte[] bytes = hf.parseHex(hexString);

        return new ByteString(bytes);
    }

    public int size()
    {
        return bytes.length;
    }
    public int byteAt(int index)
    {
        if (index < 0 || index > (bytes.length -1))
        {
            throw new IndexOutOfBoundsException();
        }

        return bytes[index] & 0xff;
    }
    public long bytesInRange(int fromIndex, int toIndex)
    {
        Objects.checkFromToIndex(fromIndex, toIndex, this.size());

        if (toIndex - fromIndex >= Long.SIZE)
        {
            throw new IllegalArgumentException();
        }

        long mask = bytes[fromIndex];

        for (int i = (fromIndex + 1); i < toIndex; i++)
        {
            mask = ((mask << 8) | (bytes[i] & 0xFF));
        }

        return mask;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof ByteString) && (Arrays.equals(((ByteString) obj).bytes, this.bytes ));
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString()
    {
        return hf.formatHex(bytes);
    }
}
