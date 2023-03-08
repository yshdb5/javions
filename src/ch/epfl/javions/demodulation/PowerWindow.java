package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class PowerWindow
{
    private final static int BATCHSIZE = 1 << 16;
    private int windowSize;
    private int position;
    private int count;
    private PowerComputer computer;
    private int [] evenBatch;
    private int [] oddBatch;


    public PowerWindow(InputStream stream, int windowSize) throws IOException
    {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= Math.scalb(1, 16)));

        this.windowSize = windowSize;
        position = 0;

        computer = new PowerComputer(stream, BATCHSIZE);

        evenBatch = new int[BATCHSIZE];
        oddBatch = new int[BATCHSIZE];

        count = computer.readBatch(evenBatch);
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
        return count >= windowSize;
    }

    public int get(int i)
    {
        Objects.checkIndex(i, windowSize);

        if (((position % BATCHSIZE) + i) < BATCHSIZE)
        {
            return evenBatch [(position % BATCHSIZE) + i];
        }
        else
        {
            return oddBatch[(position % BATCHSIZE) + i];
        }
    }

    public void advance() throws IOException
    {
        position++;
        count--;

        if ((position + windowSize - 1) == BATCHSIZE)
        {
            count += computer.readBatch(oddBatch);
        }
        else if (position % BATCHSIZE == 0)
        {
            int [] temp = evenBatch;
            evenBatch = oddBatch;
            oddBatch = temp;
        }
    }
    public void advanceBy(int offset) throws IOException
    {
        Preconditions.checkArgument(offset >= 0);

        for (int i = 0; i < offset; i++)
        {
            advance();
        }
    }
}
