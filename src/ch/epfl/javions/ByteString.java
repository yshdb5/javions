package ch.epfl.javions;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/02/23
 */

public final class ByteString
{
    public ByteString(byte[] bytes)
    {

    }

    public static ByteString ofHexadecimalString(String hexString)
    {
        if ((hexString.length() % 2) != 0)
        {
            throw new IllegalArgumentException();
        }
    }
}
