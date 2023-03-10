package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.PowerComputer;
import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    @Test
    void test() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer = new PowerComputer(stream, 8);
        int[] actualBatch = new int[8];
        int actualCalculated = powerComputer.readBatch(actualBatch);
        int[] expectedBatch = new int[] {73, 292, 65, 745, 98, 4226, 12244, 25722};
        int expectedCalculated = 8;
        assertEquals(expectedCalculated, actualCalculated);
        assertArrayEquals(expectedBatch, actualBatch);
        expectedBatch=new int[]{36818, 23825, 10730, 1657, 1285, 1280, 394, 521};
        actualCalculated = powerComputer.readBatch(actualBatch);
        assertArrayEquals(expectedBatch, actualBatch);
    }

    @Test
    void testValidPowerComputer() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2 , StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);
        SamplesDecoder test = new SamplesDecoder(stream, 2402);
        PowerComputer test2 = new PowerComputer(stream , 9608);

        short [] batch = new short[2402];
        int [] batch2 = new int[9608];
        int a = test2.readBatch(batch2);
        for (int i = 0; i < 10 ; ++i) {
            System.out.println(batch2[i]);
        }
    }

}