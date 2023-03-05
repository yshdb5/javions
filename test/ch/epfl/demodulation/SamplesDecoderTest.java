package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {

    @Test
    void SamplesDecoderWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");

        SamplesDecoder decoder =  new SamplesDecoder(stream, 16);

        short [] shortTab = new short[16];

        decoder.readBatch(shortTab);

        System.out.println("Decoder Tab: " +Arrays.toString(shortTab));
    }
}