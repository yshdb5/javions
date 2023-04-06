package ch.epfl.javions.demodulation;


import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * final class PowerComputer : represents an object able to calculate the power samples of the signal
 * from the signed samples produced by a sample decoder
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class PowerComputer {
    private static final int VALUES_IN_SAMPLE = 8;
    private final SamplesDecoder decoder;
    private final short[] samplesBatchTab;
    private final int batchSize;
    private final short[] lastEightTab = new short[VALUES_IN_SAMPLE];
    private int head = 0;

    /**
     * PowerComputer's constructor
     * creates the array that contains the samples
     *
     * @param stream    the stream
     * @param batchSize the batch size
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument((batchSize % VALUES_IN_SAMPLE) == 0);

        this.batchSize = batchSize;
        samplesBatchTab = new short[2 * batchSize];
        decoder = new SamplesDecoder(stream, 2 * batchSize);
    }

    /**
     * reads from the sample decoder the number of samples needed to calculate a batch
     * of power sample and then computes them
     *
     * @param batch a batch of length 1 << 16
     * @return the number of power samples placed in the array
     * @throws IOException in case of input/output error
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument((batch.length == batchSize) && batchSize > 0);

        int samplesNumber = decoder.readBatch(samplesBatchTab);
        int count = 0;

        for (int i = 0, j = 0; i < (samplesNumber - 1); i += 2, j++) {
            head = (head + 1) % VALUES_IN_SAMPLE;
            lastEightTab[head] = samplesBatchTab[i];
            head = (head + 1) % VALUES_IN_SAMPLE;
            lastEightTab[head] = samplesBatchTab[i + 1];

            batch[j] = calculatedPower();
            count++;
        }
        return count;
    }

    private int calculatedPower() {
        int evenSum = 0;
        int oddSum = 0;

        for (int i = 0; i < VALUES_IN_SAMPLE; i++) {
            int lastIndex = lastEightTab[(head - i + VALUES_IN_SAMPLE) % VALUES_IN_SAMPLE];

            if (i % 2 == 0) {
                evenSum = (i % 4 == 0) ? evenSum - lastIndex : evenSum + lastIndex;
            } else {
                oddSum = (i % 4 == 1) ? oddSum + lastIndex : oddSum - lastIndex;
            }
        }

        return evenSum * evenSum + oddSum * oddSum;
    }
}
