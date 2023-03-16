package ch.epfl.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AircraftIdentificationMessage;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

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
        System.out.println(AircraftIdentificationMessage.of(message));
    }
}