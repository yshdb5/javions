package ch.epfl.javions.demodulation;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        03/03/23
 */

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class PowerWindow
{
    private int windowSize;
    private int position;
    private PowerComputer computer;
    private int [] evenBatch;
    private int [] oddBatch;


    public PowerWindow(InputStream stream, int windowSize) throws IOException
    {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= Math.scalb(1, 16)));

        this.windowSize = windowSize;
        position = 0;

        computer = new PowerComputer(stream, windowSize);

        evenBatch = new int[windowSize];
        oddBatch = new int[windowSize];

        computer.readBatch(evenBatch);
    }

    public int size()
    {
        return windowSize;
    }

    public long position()
    {
        return position;
    }

    public boolean isFull()
    {
        return false;
    }

    public int get(int i)
    {
        Objects.checkIndex(i, windowSize);

        return 1;
    }

    public void advance() throws IOException
    {
        position++;
    }
    public void advanceBy(int offset) throws IOException
    {
        position += offset;
    }
}
