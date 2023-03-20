package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.PowerWindow;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest
{
    @Test
    void PowerWindowThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");

        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, 0));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, 1 << 17));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, -1));
        assertDoesNotThrow(() -> new PowerWindow(stream, 1<<16));
    }

    @Test
    void SizeWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 12);
        int actual = window.size();
        int expected = 12;
        assertEquals(expected, actual);
    }

    @Test
    void PositionWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 100);
        window.advance();
        window.advanceBy(5);
        window.advanceBy(0);
        long actual = window.position();
        long expected = 6;
        assertEquals(expected, actual);
    }

    @Test
    void IsFullWorksOnKnownValues1() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        boolean actual = window.isFull();
        boolean expected = true;
        assertEquals(expected, actual);
    }

    @Test
    void IsFullWorksOnKnownValues2() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1202);
        boolean actual = window.isFull();
        boolean expected = false;
        assertEquals(expected, actual);
    }

    @Test
    void IsFullWorksOnKnownValues3() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        window.advance();
        boolean actual = window.isFull();
        boolean expected = false;
        assertEquals(expected, actual);
    }

    @Test
    void GetIWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        window.advanceBy(2);
        int actual = window.get(1);
        int expected = 745;
        assertEquals(expected, actual);

        window.advanceBy(48);
        actual = window.get(0);
        expected = 160;
        assertEquals(expected, actual);
    }

    @Test
    void GetIWorksOnKnownValues2() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 10);
        int actual = window.get(0);
        int expected = 73;
        assertEquals(expected, actual);
        window.advanceBy(2);
        actual = window.get(0);
        expected = 65;
        assertEquals(expected, actual);
        actual = window.get(9);
        expected = 1657;
        assertEquals(expected, actual);
    }

    @Test
    void GetIThrowsIndexOutOfBoundsException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(1201));
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(1202));
        assertDoesNotThrow(() -> window.get(0));
    }

    @Test
    void AdvanceByThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);

        assertThrows(IllegalArgumentException.class, () -> window.advanceBy(-1));
        assertDoesNotThrow(() -> window.advanceBy(0));
    }

    @Test
    void checkGet() throws IOException
    {
        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, StandardCharsets.UTF_8);
        InputStream file=new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(file, 2);
        powerWindow.advanceBy(8);
        assertEquals(23825, powerWindow.get(1));
        file.close();
    }

    @Test
    public void checkAnotherGet() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2 , StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);

        PowerWindow powerWindow = new PowerWindow(stream, 5);
        powerWindow.advanceBy(7);
        assertEquals(1657,powerWindow.get(4));

    }

    private static final int BATCH_SIZE = 1 << 16;
    private static final int BATCH_SIZE_BYTES = bytesForPowerSamples(BATCH_SIZE);
    private static final int STANDARD_WINDOW_SIZE = 1200;
    private static final int BIAS = 1 << 11;

    private static int bytesForPowerSamples(int powerSamplesCount) {
        return powerSamplesCount * 2 * Short.BYTES;
    }

    @Test
    void powerWindowConstructorThrowsWithInvalidWindowSize() throws IOException {
        try (var s = InputStream.nullInputStream()) {
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, 0));
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, -1));
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, (1 << 16) + 1));
        }
    }

    @Test
    void powerWindowSizeReturnsWindowSize() throws IOException {
        try (var s = InputStream.nullInputStream()) {
            for (var i = 1; i <= 1 << 16; i <<= 1) {
                var w = new PowerWindow(s, i);
                assertEquals(i, w.size());
            }
        }
    }

    @Test
    void powerWindowPositionIsCorrectlyUpdatedByAdvance() throws IOException {
        var batches16 = new byte[BATCH_SIZE_BYTES * 16];
        try (var s = new ByteArrayInputStream(batches16)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            var expectedPos = 0L;

            assertEquals(expectedPos, w.position());

            w.advance();
            expectedPos += 1;
            assertEquals(expectedPos, w.position());

            w.advanceBy(BATCH_SIZE);
            expectedPos += BATCH_SIZE;
            assertEquals(expectedPos, w.position());

            w.advanceBy(BATCH_SIZE - 1);
            expectedPos += BATCH_SIZE - 1;
            assertEquals(expectedPos, w.position());

            w.advance();
            expectedPos += 1;
            assertEquals(expectedPos, w.position());
        }
    }

    @Test
    void powerWindowAdvanceByCanAdvanceOverSeveralBatches() throws IOException {
        var bytes = bytesForZeroSamples(16);

        var batchesToSkipOver = 2;
        var inBatchOffset = 37;
        var offset = batchesToSkipOver * BATCH_SIZE + inBatchOffset;
        var sampleBytes = Base64.getDecoder().decode(PowerComputerTest.SAMPLES_BIN_BASE64);
        System.arraycopy(sampleBytes, 0, bytes, bytesForPowerSamples(offset), sampleBytes.length);

        try (var s = new ByteArrayInputStream(bytes)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            w.advanceBy(inBatchOffset);
            w.advanceBy(batchesToSkipOver * BATCH_SIZE);
            var expected = Arrays.copyOfRange(PowerComputerTest.POWER_SAMPLES, 0, STANDARD_WINDOW_SIZE);
            var actual = new int[STANDARD_WINDOW_SIZE];
            for (var i = 0; i < STANDARD_WINDOW_SIZE; i += 1) actual[i] = w.get(i);
            assertArrayEquals(expected, actual);
        }
    }

    @Test
    void powerWindowIsFullWorks() throws IOException {
        var twoBatchesPlusOneWindowBytes =
                bytesForPowerSamples(BATCH_SIZE * 2 + STANDARD_WINDOW_SIZE);
        var twoBatchesPlusOneWindow = new byte[twoBatchesPlusOneWindowBytes];
        try (var s = new ByteArrayInputStream(twoBatchesPlusOneWindow)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(BATCH_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(BATCH_SIZE);
            assertTrue(w.isFull());

            w.advance();
            assertFalse(w.isFull());
        }
    }

    @Test
    void powerWindowGetWorksOnGivenSamples() throws IOException {
        try (var sampleStream = PowerComputerTest.getSamplesStream()) {
            var windowSize = 100;
            var w = new PowerWindow(sampleStream, windowSize);
            for (var offset = 0; offset < 100; offset += 1) {
                var expected = Arrays.copyOfRange(PowerComputerTest.POWER_SAMPLES, offset, offset + windowSize);
                var actual = new int[windowSize];
                for (var i = 0; i < windowSize; i += 1) actual[i] = w.get(i);
                assertArrayEquals(expected, actual);
                w.advance();
            }
        }
    }

    @Test
    void powerWindowGetWorksAcrossBatches() throws IOException {
        byte[] bytes = bytesForZeroSamples(2);
        var firstBatchSamples = STANDARD_WINDOW_SIZE / 2 - 13;
        var offset = BATCH_SIZE_BYTES - bytesForPowerSamples(firstBatchSamples);
        var sampleBytes = Base64.getDecoder().decode(PowerComputerTest.SAMPLES_BIN_BASE64);
        System.arraycopy(sampleBytes, 0, bytes, offset, sampleBytes.length);
        try (var s = new ByteArrayInputStream(bytes)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            w.advanceBy(BATCH_SIZE - firstBatchSamples);
            for (int i = 0; i < STANDARD_WINDOW_SIZE; i += 1)
                assertEquals(PowerComputerTest.POWER_SAMPLES[i], w.get(i));
        }
    }

    private static byte[] bytesForZeroSamples(int batchesCount) {
        var bytes = new byte[BATCH_SIZE_BYTES * batchesCount];

        var msbBias = BIAS >> Byte.SIZE;
        var lsbBias = BIAS & ((1 << Byte.SIZE) - 1);
        for (var i = 0; i < bytes.length; i += 2) {
            bytes[i] = (byte) lsbBias;
            bytes[i + 1] = (byte) msbBias;
        }
        return bytes;
    }
}