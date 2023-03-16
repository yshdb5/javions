package ch.epfl.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class AirbornePositionMessageTest
{
    private final static HexFormat hf = HexFormat.of();
    private final static byte [] tab = hf.parseHex("8D49529958B302E6E15FA352306B");
    private final static ByteString bytes = new ByteString(tab);
    private final static long timeStamp = 75898000;
    private static final RawMessage message = new RawMessage(timeStamp, bytes);

    @Test
    void ofWorksOnKnownValues()
    {
        AirbornePositionMessage PositionMessage = AirbornePositionMessage.of(message);
        int expectedTimstamp = 75898000;
        IcaoAddress expectedICAO = new IcaoAddress("495299");
        double expectedAltitude = 10546.08;
        double expectedParity = 0;
        double expectedX = 0.6867904663085938;
        double expectedY = 0.7254638671875;

        assertEquals(expectedTimstamp, PositionMessage.timeStampNs());
        assertEquals(expectedICAO, PositionMessage.icaoAddress());
        assertEquals(expectedAltitude, PositionMessage.altitude());
        assertEquals(expectedParity, PositionMessage.parity());
        assertEquals(expectedX, PositionMessage.x());
        assertEquals(expectedY, PositionMessage.y());
    }
}