package ch.epfl.javions.demodulation;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        03/03/23
 */

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer
{
    private InputStream stream;
    private SamplesDecoder decoder;
    private short [] samplesbatchTab;
    private int batchSize;

    private short [] lastEightTab =  new short[8];
    private int head = 0;

    public PowerComputer(InputStream stream, int batchSize)
    {
        Preconditions.checkArgument((batchSize % 8) == 0);

        this.stream = stream;
        this.batchSize = batchSize;
        samplesbatchTab = new short [batchSize];
        decoder = new SamplesDecoder(stream, batchSize);
    }

    public int readBatch(int[] batch) throws IOException
    {
        Preconditions.checkArgument(batch.length == batchSize);

        int samplesNumber = decoder.readBatch(samplesbatchTab);
        int count = 0;

        for (int i = 0; i < (samplesNumber - 1); i += 2)
        {
            head = (head + 1) % 8;
            lastEightTab[head] = samplesbatchTab[i];
            head = (head + 1) % 8;
            lastEightTab[head] = samplesbatchTab[i+1];

            batch [i] = calculatedPower();
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
                evenSum += lastIndex;
            }
            else
            {
                oddSum += lastIndex; //faire en sorte d'alterner les +-
            }
        }

        return evenSum*evenSum + oddSum*oddSum;
    }
}
