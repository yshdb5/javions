package ch.epfl.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.AircraftIdentificationMessage;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class AircraftIdentificationMessageTest
{
    private final static HexFormat hf = HexFormat.of();
    private final static byte [] tab = hf.parseHex("8D4D2228234994B7284820323B81");
    private final static ByteString bytes = new ByteString(tab);
    private final static long timeStamp = 1499146900;
    private static final RawMessage message = new RawMessage(timeStamp, bytes);

    @Test
    void ofWorksOnKnownValues()
    {
    AircraftIdentificationMessage identificationMessage = AircraftIdentificationMessage.of(message);
        int expectedTimstamp = 1499146900;
        IcaoAddress expectedICAO = new IcaoAddress("4D2228");
        int expectedCategory = 163;
        CallSign expectedCallSign = new CallSign("RYR7JD");

        assertEquals(expectedTimstamp, identificationMessage.timeStampNs());
        assertEquals(expectedICAO, identificationMessage.icaoAddress());
        assertEquals(expectedCategory, identificationMessage.category());
        assertEquals(expectedCallSign, identificationMessage.callSign());

    }

    private final static byte [] tab1 = hf.parseHex("8F01024C233530F3CF6C60A19669");
    private final static ByteString bytes1 = new ByteString(tab1);
    private final static long timeStamp1 = 2240535600L;
    private static final RawMessage message1 = new RawMessage(timeStamp1, bytes1);

    @Test
    void ofWorksOnKnownValues2()
    {
        AircraftIdentificationMessage identificationMessage = AircraftIdentificationMessage.of(message1);
        long expectedTimstamp = 2240535600L;
        IcaoAddress expectedICAO = new IcaoAddress("01024C");
        int expectedCategory = 163;
        CallSign expectedCallSign = new CallSign("MSC3361");

        assertEquals(expectedTimstamp, identificationMessage.timeStampNs());
        assertEquals(expectedICAO, identificationMessage.icaoAddress());
        assertEquals(expectedCategory, identificationMessage.category());
        assertEquals(expectedCallSign, identificationMessage.callSign());

    }

    @Test
    void PrintAircraftIdentificationMessage() throws IOException
    {
        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {

            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null)
            {
                if ((m.typeCode() >= 1 && m.typeCode() <= 4))
                {
                    System.out.println(AircraftIdentificationMessage.of(m));
                }
            }
        }
    }
}