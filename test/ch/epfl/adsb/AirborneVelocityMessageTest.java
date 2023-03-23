package ch.epfl.adsb;

import ch.epfl.javions.adsb.AirborneVelocityMessage;
import ch.epfl.javions.adsb.AircraftIdentificationMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AirborneVelocityMessageTest {

    @Test
    void PrintAirborneVelocityMessage() throws IOException
    {
        int count = 0;
        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {

            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null)
            {
                if ((m.typeCode() == 19))
                {
                    System.out.println(AirborneVelocityMessage.of(m));
                    count++;
                }
            }
        }
        System.out.println(count);
    }

    /*
    Données des 5 premiers messages:

    velocity: 217.1759987875795
    track or heading: 5.707008696317668


    velocity: 227.75426436901594
    track or heading: 4.1068443167797195


    velocity: 161.15254486753832
    track or heading: 3.9337627224977503


    velocity: 228.01904908511267
    track or heading: 5.311655187675027


    velocity: 114.64264880353804
    track or heading: 5.335246702497837
     */
}