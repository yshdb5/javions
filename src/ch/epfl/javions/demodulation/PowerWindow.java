package ch.epfl.javions.demodulation;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        03/03/23
 */

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow
{
    private int windowSize;

    public PowerWindow(InputStream stream, int windowSize) throws IOException
    {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= Math.scalb(1, 16)));

        this.windowSize = windowSize;
    }

    public int size()
    {
        return windowSize;
    }

    public long position()
    {
        return 1;
    }

    public boolean isFull()
    {
        return false;
    }

    public int get(int i)
    {
        return 1;
    }

    public void advance() throws IOException
    {

    }

    public void advanceBy(int offset) throws IOException
    {

    }
}
