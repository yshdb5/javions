package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * final class PowerWindow : represents a window of fixed size over a sequence of power samples
 *                           produced by a power computer
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class PowerWindow
{
    private final static int BATCHSIZE = 1 << 16;
    private int windowSize;
    private int position;
    private int count;
    private PowerComputer computer;
    private int [] evenBatch;
    private int [] oddBatch;

    /**
     * PowerWindow's constructor, returns a window of given size on the sequence of power samples
     *                                   computed from the bytes providedby the given input stream
     * @param stream
     * @param windowSize
     * @throws IOException
     * @throws IllegalArgumentException if the window's size isnt between O (excluded) and 2 power 16 (included)
     */

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

    /**
     * @return the window's size
     */
    public int size()
    {
        return windowSize;
    }

    /**
     * @return the current position of the window relative to the beginning of the power value stream
     */
    public long position()
    {
        return position;
    }

    /**
     * @return true if and only if the window contains as many samples as its size
     */
    public boolean isFull()
    {
        return count >= windowSize;
    }

    /**
     * @param i
     *       the index
     * @return the power sample at the given index (i) of the window
     * @throws IndexOutOfBoundsException if i isnt between 0 included and the window's size excluded
     */
    public int get(int i)
    {
        Objects.checkIndex(i, windowSize);

        if (((position % BATCHSIZE) + i) < BATCHSIZE)
        {
            return evenBatch [(position % BATCHSIZE) + i];
        }
        else
        {
            return oddBatch[(position % BATCHSIZE) + i - BATCHSIZE];
        }
    }

    /**
     * advances the window of a sample
     * @throws IOException
     */
    public void advance() throws IOException
    {
        position++;
        count--;

        if ((position + windowSize) % BATCHSIZE == 0)
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

    /**
     * advances the window by the given number of samples
     * @param offset
     *        the given number of samples
     * @throws IOException
     * @throws IllegalArgumentException if offset isnt >=0
     */
    public void advanceBy(int offset) throws IOException
    {
        Preconditions.checkArgument(offset >= 0);

        for (int i = 0; i < offset; i++)
        {
            advance();
        }
    }
}
