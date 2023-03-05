package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest
{
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
}