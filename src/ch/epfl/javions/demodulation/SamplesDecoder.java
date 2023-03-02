package ch.epfl.javions.demodulation;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        01/03/23
 */

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SamplesDecoder
{
    private InputStream stream;

    private byte [] batchTab;
    private int batchSize;

    public SamplesDecoder(InputStream stream, int batchSize)
    {
        Preconditions.checkArgument(batchSize > 0);
        Objects.requireNonNull(stream);

        this.stream = stream;

        batchTab = new byte[2*batchSize];
        this.batchSize = batchSize;

    }

    public int readBatch (short [] batch) throws IOException
    {
        Preconditions.checkArgument(batch.length == batchSize);

        //stream.readNBytes(batch, 0, batchSize);
        return 1;
    }

}
