package ch.epfl.javions;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/02/23
 */

import java.util.Objects;

public class Bits
{
    private Bits(){}

    public static int extractUInt(long value, int start, int size)
    {
        if (size <= 0 || size >= Integer.SIZE)
        {
            throw new IllegalArgumentException();
        }

        Objects.checkFromIndexSize(start, size, Long.SIZE);

        long mask = (1L << size) - 1;
        long extractedValue = (value >>> start) & mask;
        return (int) extractedValue;
    }

    public static boolean testBit(long value, int index)
    {
        Objects.checkIndex(index, Long.SIZE);

        long mask = 1L << index;
        return (value & mask) != 0;
    }
}
