package ch.epfl.javions.demodulation;


import ch.epfl.javions.Preconditions;
import ch.epfl.javions.demodulation.SamplesDecoder;

import java.io.IOException;
import java.io.InputStream;

/**
 * final class PowerComputer : represents an object able to calculate the power samples of the signal
 *                            from the signed samples produced by a sample decoder
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class PowerComputer
{
    private InputStream stream;
    private SamplesDecoder decoder;
    private short [] samplesbatchTab;
    private int batchSize;
    private short [] lastEightTab =  new short[8];
    private int head = 0;

    /**
     * PowerComputer's constructor
     *                 creates the array that contains the samples
     * @param stream
     * @param batchSize
     */
    public PowerComputer(InputStream stream, int batchSize)
    {
        Preconditions.checkArgument((batchSize % 8) == 0);

        this.stream = stream;
        this.batchSize = batchSize;
        samplesbatchTab = new short [2*batchSize];
        decoder = new SamplesDecoder(stream, 2*batchSize);
    }

    /**
     * reads from the sample decoder the number of samples needed to calculate a batch
     *                                           of power sample and then computes them
     * @param batch
     * @return the number of power samples placed in the array
     * @throws IOException
     *          in case of input/output error
     */
    public int readBatch(int[] batch) throws IOException
    {
        Preconditions.checkArgument((batch.length == batchSize) && batchSize > 0);

        int samplesNumber = decoder.readBatch(samplesbatchTab);
        int count = 0;

        for (int i = 0, j = 0; i < (samplesNumber - 1); i += 2, j++)
        {
            head = (head + 1) % 8;
            lastEightTab[head] = samplesbatchTab[i];
            head = (head + 1) % 8;
            lastEightTab[head] = samplesbatchTab[i+1];

            batch [j] = calculatedPower();
            count++;
        }
        return count;
    }

    private int calculatedPower()
    {
        int evenSum = 0;
        int oddSum = 0;

        for (int i = 0; i < 8; i++)
        {
            int lastIndex = lastEightTab[(head - i + 8) % 8];

            if (i % 2 == 0)
            {
                if (i % 4 == 0)
                {
                    evenSum -= lastIndex;
                }
                else
                {
                    evenSum += lastIndex;
                }
            }
            else
            {
                if (i % 4 == 1)
                {
                    oddSum += lastIndex;
                }
                else
                {
                    oddSum -= lastIndex;
                }
            }
        }

        return evenSum*evenSum + oddSum*oddSum;
    }
}
