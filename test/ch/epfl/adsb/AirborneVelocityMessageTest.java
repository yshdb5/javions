package ch.epfl.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AirborneVelocityMessage;
import ch.epfl.javions.adsb.AircraftIdentificationMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class AirborneVelocityMessageTest
{
    private final static HexFormat hf = HexFormat.of();
    private final static byte [] tab1 = hf.parseHex("8D485020994409940838175B284F");
    private final static ByteString bytes1 = new ByteString(tab1);
    private final static long timeStamp1 = 75898000;
    private static final RawMessage message = new RawMessage(timeStamp1, bytes1);
    @Test
    void AirborneVelocityMessageWorksOnGivenValues()
    {
        AirborneVelocityMessage message1 = AirborneVelocityMessage.of(message);

        double velocity = 0;
        double track = 0;
        //assertEquals(velocity, message1.speed());
        //assertEquals(track, message1.trackOrHeading());

    }

    private final static byte [] tab2 = hf.parseHex("8D485020994409940838175B284F");
    private final static ByteString bytes2 = new ByteString(tab2);
    private final static long timeStamp2 = 75898000;
    private static final RawMessage message2 = new RawMessage(timeStamp2, bytes2);

    @Test
    void AirborneVelocityMessageWorksOnGivenValues2()
    {
        AirborneVelocityMessage message2 = AirborneVelocityMessage.of(message);

        double velocity = 0;
        double track = 0;
        //assertEquals(velocity, message2.speed());
        //assertEquals(track, message2.trackOrHeading());

    }
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