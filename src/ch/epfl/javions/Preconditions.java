package ch.epfl.javions;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/02/23
 */

public final class Preconditions
{
    private Preconditions(){}

    public static void checkArgument(boolean shouldBeTrue)
    {
        if (!shouldBeTrue)
        {
            throw new IllegalArgumentException();
        }
    }
}
