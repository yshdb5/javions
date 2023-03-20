package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest
{
    /*
    @Test
    void SamplesDecoderWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder decoder =  new SamplesDecoder(stream, 16);

        short [] shortTab = new short[16];

        decoder.readBatch(shortTab);

        System.out.println("Decoder Tab: " +Arrays.toString(shortTab));
    }

    @Test
    void SamplesDecoderThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");

        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(stream, -1));
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(stream, 0));
    }

    @Test
    void SamplesDecoderThrowsNullPointerException()
    {
        FileInputStream stream = null;

        assertThrows(NullPointerException.class, () -> new SamplesDecoder(stream, 1));
    }

    @Test
    void readbatchThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder decoder =  new SamplesDecoder(stream, 16);
        short [] shortTab = new short[10];

        assertThrows(IllegalArgumentException.class, () -> decoder.readBatch(shortTab));
    }

    @Test
    void readbatchReturnsTheNumberOfEchantillonsRead() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder decoder =  new SamplesDecoder(stream, 16);
        short [] shortTab = new short[16];
        int actual = decoder.readBatch(shortTab);
        int expected = 16;

        assertEquals(expected, actual);
    }

    @Test
    void readbatchReturnsTheNumberOfEchantillonsRead2() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder decoder =  new SamplesDecoder(stream, 5000);
        short [] shortTab = new short[5000];
        int actual = decoder.readBatch(shortTab);
        int expected = 2402;

        assertEquals(expected, actual);
    }

    @Test
    void CheckSamplesDecoderReturnsWell() throws IOException {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder decoder = new SamplesDecoder(stream, 10);
        short[] batch = new short[10];
        short[] expected = {-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        decoder.readBatch(batch);
        for( int i = 0; i < 10; i++)
            assertEquals(expected[i], batch[i]);
    }

     */


    private static final int SAMPLES_COUNT = 1 << 12;
    private static final int BIAS = 1 << 11;

    private static byte[] getSampleBytes() {
        var sampleBytes = new byte[SAMPLES_COUNT * Short.BYTES];
        var sampleBytesBuffer = ByteBuffer.wrap(sampleBytes)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer();

        for (int i = 0; i < SAMPLES_COUNT; i += 1)
            sampleBytesBuffer.put((short) i);
        return sampleBytes;
    }

    @Test
    void samplesDecoderConstructorThrowsWithInvalidBatchSize() {
        var stream = new ByteArrayInputStream(new byte[0]);
        assertThrows(
                IllegalArgumentException.class,
                () -> new SamplesDecoder(stream, -1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new SamplesDecoder(stream, 0));
    }

    @Test
    void samplesDecoderConstructorThrowsWithNullStream() {
        assertThrows(
                NullPointerException.class,
                () -> new SamplesDecoder(null, 1));
    }

    @Test
    void samplesDecoderReadBatchThrowsOnInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            try (var byteStream = new ByteArrayInputStream(getSampleBytes())) {
                var batchSize = 1024;
                var actualSamples = new short[batchSize - 1];
                var samplesDecoder = new SamplesDecoder(byteStream, batchSize);
                samplesDecoder.readBatch(actualSamples);
            }
        });
    }

    @Test
    void samplesDecoderReadBatchCorrectlyReadsSamples() throws IOException {
        try (var byteStream = new ByteArrayInputStream(getSampleBytes())) {
            var expectedSamples = new short[SAMPLES_COUNT];
            for (int i = 0; i < SAMPLES_COUNT; i += 1)
                expectedSamples[i] = (short) (i - BIAS);

            var actualSamples = new short[SAMPLES_COUNT];
            var samplesDecoder = new SamplesDecoder(byteStream, actualSamples.length);
            var readSamples = samplesDecoder.readBatch(actualSamples);
            assertEquals(SAMPLES_COUNT, readSamples);
            assertArrayEquals(expectedSamples, actualSamples);
        }
    }

    @Test
    void samplesDecoderWorksWithDifferentBatchSizes() throws IOException {
        var expectedSamples = new short[SAMPLES_COUNT];
        for (int i = 0; i < SAMPLES_COUNT; i += 1)
            expectedSamples[i] = (short) (i - BIAS);

        for (var batchSize = 1; batchSize < SAMPLES_COUNT; batchSize *= 2) {
            try (var byteStream = new ByteArrayInputStream(getSampleBytes())) {
                var samplesDecoder = new SamplesDecoder(byteStream, batchSize);
                var actualSamples = new short[SAMPLES_COUNT];
                var batch = new short[batchSize];
                for (var i = 0; i < SAMPLES_COUNT / batchSize; i += 1) {
                    var samplesRead = samplesDecoder.readBatch(batch);
                    assertEquals(batchSize, samplesRead);
                    System.arraycopy(batch, 0, actualSamples, i * batchSize, batchSize);
                }
                assertArrayEquals(expectedSamples, actualSamples);
            }
        }
    }
}