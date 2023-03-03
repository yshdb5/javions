package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.PowerComputer;
import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PowerComputerTest
{

    @Test
    void SamplesDecoderWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");

        PowerComputer powerComputer =  new PowerComputer(stream, 16);

        int [] powerTab = new int[16];

        powerComputer.readBatch(powerTab);

        System.out.println(Arrays.toString(powerTab));
    }
}