package ch.epfl.javions.demodulation;
/**
 * final class SampleDecoder: represents an object capable of transforming the bytes coming from
 * AirSpy into signed 12-bit samples .
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SamplesDecoder {
    private final static double RECENTER_VALUE = Math.scalb(1, 11);
    private final InputStream stream;
    private final byte[] batchTab;
    private final int batchSize;

    /**
     * returns a sample decoder using the given input stream to get the bytes from
     * the Airspy radio and producing the samples in batches of given size
     *
     * @param stream    the given input stream
     * @param batchSize the number of samples to be produced during each conversion
     * @throws IllegalArgumentException if bachSize <=0
     * @throws NullPointerException     if the stream is null
     */

    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);
        Objects.requireNonNull(stream);

        this.stream = stream;

        this.batchSize = batchSize;
        batchTab = new byte[2 * batchSize];
    }

    /**
     * reads from the stream passed to the constructor the number of bytes corresponding to a batch,
     * then converts these bytes into signed samples
     *
     * @param batch the batch we want to read
     * @return the number of converted samples
     * @throws IOException              in case of input/output error
     * @throws IllegalArgumentException if the size of the array passed in argument is not equal to the batch size
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);

        int samplesCount = stream.readNBytes(batchTab, 0, batchSize * 2) / (Short.BYTES);

        for (int i = 0, j = 0; i < samplesCount * 2; i += 2, j++) {
            byte byte1 = batchTab[i];
            byte byte2 = batchTab[i + 1];

            short short1 = (short) (((Byte.toUnsignedInt(byte2) << Byte.SIZE) | Byte.toUnsignedInt(byte1)) - RECENTER_VALUE);
            batch[j] = short1;
        }

        return samplesCount;
    }

}
