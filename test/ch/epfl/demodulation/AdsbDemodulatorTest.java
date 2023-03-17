package ch.epfl.demodulation;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AdsbDemodulatorTest
{
/*
    @Test
    void nextMessageTest()throws IOException
    {
        int count = 0;
        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null)
            {
                System.out.println(m);
                count++;
            }
            System.out.println(count);
        }
    }

 */
}