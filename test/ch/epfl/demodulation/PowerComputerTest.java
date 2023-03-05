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
    void PowerComputerOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer =  new PowerComputer(stream, 1208);

        int [] powerTab = new int[1208];

        powerComputer.readBatch(powerTab);

        System.out.println("PowerTab: " + Arrays.toString(powerTab));
    }

    @Test
    void PowerComputerThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");

        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(stream, 5));
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(stream, 0));
    }

    @Test
    void readbatchThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer =  new PowerComputer(stream, 1208);
        int [] shortTab = new int[10];

        assertThrows(IllegalArgumentException.class, () -> powerComputer.readBatch(shortTab));
    }

    @Test
    void readbatchReturnsTheNumberOfEchantillonsRead() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer =  new PowerComputer(stream, 1208);
        int [] shortTab = new int[1208];

        int actual = powerComputer.readBatch(shortTab);
        int expected = 1201;

        assertEquals(expected, actual);
    }

    @Test
    void readbatchReturnsTheNumberOfEchantillonsRead2() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer =  new PowerComputer(stream, 56);
        int [] shortTab = new int[56];

        int actual = powerComputer.readBatch(shortTab);
        int expected = 56;

        assertEquals(expected, actual);
    }
}