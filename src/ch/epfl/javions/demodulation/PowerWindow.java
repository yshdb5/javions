package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * final class PowerWindow : represents a window of fixed size over a sequence of power samples
 * produced by a power computer
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class PowerWindow {
    private final static int BATCH_SIZE = 1 << 16;
    private final int windowSize;
    private final PowerComputer computer;
    private int position;
    private int count;
    private int[] evenBatch;
    private int[] oddBatch;

    /**
     * PowerWindow's constructor, returns a window of given size on the sequence of power samples
     * computed from the bytes provided by the given input stream
     *
     * @param stream the input stream use to compute the power samples
     * @param windowSize the size of the window
     * @throws IOException if there is an input/output error
     * @throws IllegalArgumentException if the window's size isn't between O (excluded) and 2 power 16 (included)
     */

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= Math.scalb(1, 16)));

        this.windowSize = windowSize;
        position = 0;

        computer = new PowerComputer(stream, BATCH_SIZE);

        evenBatch = new int[BATCH_SIZE];
        oddBatch = new int[BATCH_SIZE];

        count = computer.readBatch(evenBatch);
    }

    /**
     * @return the window's size
     */
    public int size() {
        return windowSize;
    }

    /**
     * @return the current position of the window relative to the beginning of the power value stream
     */
    public long position() {
        return position;
    }

    /**
     * @return true if and only if the window contains as many samples as its size
     */
    public boolean isFull() {
        return count >= windowSize;
    }

    /**
     * @param i the index
     * @return the power sample at the given index (i) of the window
     * @throws IndexOutOfBoundsException if "i" isn't between 0 included and the window's size excluded
     */
    public int get(int i) {
        Objects.checkIndex(i, windowSize);

        if (((position % BATCH_SIZE) + i) < BATCH_SIZE) {
            return evenBatch[(position % BATCH_SIZE) + i];
        } else {
            return oddBatch[(position % BATCH_SIZE) + i - BATCH_SIZE];
        }
    }

    /**
     * advances the window of a sample
     *
     * @throws IOException in case of input/output error
     */
    public void advance() throws IOException {
        position++;
        count--;

        if ((position + windowSize) % BATCH_SIZE == 0) {
            count += computer.readBatch(oddBatch);
        } else if (position % BATCH_SIZE == 0) {
            int[] temp = evenBatch;
            evenBatch = oddBatch;
            oddBatch = temp;
        }
    }

    /**
     * advances the window by the given number of samples
     *
     * @param offset the given number of samples
     * @throws IOException in case of output/input error
     * @throws IllegalArgumentException if offset isn't >=0
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset >= 0);

        for (int i = 0; i < offset; i++) {
            advance();
        }
    }
}
