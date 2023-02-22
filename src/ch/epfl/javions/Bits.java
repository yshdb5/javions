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
        if (size < 0 || size > Integer.SIZE)
        {
            throw new IllegalArgumentException();
        }
        if (((start + size) < 0) || ((start + size) >= Long.SIZE))
        {
            throw new IndexOutOfBoundsException();
        }

        int mask = (1 << size) - 1;
        return (int) (value >> (start - 1)) & mask;
    }

    public static boolean testBit(long value, int index)
    {
        if (index < 0 || index >= Long.SIZE) {
            throw new IndexOutOfBoundsException();
        }


        long mask = 1L << index;
        return (value & mask) != 0;
    }
}
