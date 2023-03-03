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
    private short [] batchTab;
    private int batchSize;

    private byte [] lastEightTab =  new byte[8];

    PowerComputer(InputStream stream, int batchSize)
    {
        Preconditions.checkArgument((batchSize % 8) == 0);

        this.stream = stream;
        this.batchSize = batchSize;
        batchTab = new short [batchSize/2];
        decoder = new SamplesDecoder(stream, batchSize);
    }

    public int readBatch(int[] batch) throws IOException
    {
        Preconditions.checkArgument(batch.length == batchSize);

        int samplesNumber = decoder.readBatch(batchTab);

        for (int i = 0; i < batchSize; i++)
        {
            batch[i] = batchTab[i]*batchTab[i] + batchTab[i+1]*batchTab[i+1];
        }

        return 1;
    }
}
