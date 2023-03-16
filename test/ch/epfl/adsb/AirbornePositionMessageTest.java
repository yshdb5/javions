package ch.epfl.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.RawMessage;
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
        System.out.println(AirbornePositionMessage.of(message));
    }
}