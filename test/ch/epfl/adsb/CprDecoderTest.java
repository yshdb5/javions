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
        double x0 = Math.scalb(111600, -17);
        double y0 = Math.scalb(94445, -17);
        double x1 = Math.scalb(108865, -17);
        double y1 = Math.scalb(77558, -17);

        GeoPos pos = CprDecoder.decodePosition(x0, y0, x1, y1, 0);

        int expectedLongitude = 89192898;
        int expectedLatitude = 552659081;

        int actualLongitude = pos.longitudeT32();
        int actualLatitude = pos.latitudeT32();

        assertEquals(expectedLongitude, actualLongitude);
        assertEquals(expectedLatitude, actualLatitude);
    }

    @Test
    void decodePositionWorks2()
    {
        GeoPos actualPosition = CprDecoder.decodePosition(0.62,0.42,0.6200000000000000001,0.4200000000000000001,0);

        assertEquals("(-2.3186440486460924°, 2.5199999939650297°)", actualPosition.toString());
    }

    @Test
    void decodePositionWorks3()
    {
        GeoPos actualPosition = CprDecoder.decodePosition(0.3,0.3,0.3,0.3,0);

        assertEquals("(1.8305084947496653°, 1.7999999597668648°)", actualPosition.toString());

        GeoPos actualPosition2 = CprDecoder.decodePosition(0.3,0.3,0.3,0.3,1);

        assertEquals("(1.862068958580494°, 1.8305084947496653°)", actualPosition2.toString());
    }
}