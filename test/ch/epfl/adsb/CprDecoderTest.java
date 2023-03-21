package ch.epfl.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.CprDecoder;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class CprDecoderTest
{

    @Test
    void decodePositionWorks()
    {
        double x0 = 111600*Math.scalb(1, -17);
        double y0 = 94445*Math.scalb(1, -17);
        double x1 = 108865*Math.scalb(1, -17);
        double y1 = 77558*Math.scalb(1, -17);

        GeoPos pos = CprDecoder.decodePosition(x0, y0, x1, y1, 0);

        int expectedLongitude = 89192898;
        int expectedLatitude = 552659081;

        int actualLongitude = pos.longitudeT32();
        int actualLatitude = pos.latitudeT32();

        assertEquals(expectedLongitude, actualLongitude);
        assertEquals(expectedLatitude, actualLatitude);
    }
}