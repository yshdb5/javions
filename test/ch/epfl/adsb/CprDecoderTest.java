package ch.epfl.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
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

    @Test
    void decodePositionWorks4()
    {
        GeoPos actualPosition = CprDecoder.decodePosition(0, 0.3, 0, 0, 0);

        assertNull(actualPosition);
    }

    @Test
    public void test112()
    {
        GeoPos pos = CprDecoder.decodePosition(0.747222900390625,0.7342300415039062, 0.6243515014648438, 0.4921417236328125,0);
        System.out.println(pos);
    }
    @Test
    public void testEd()
    {
        GeoPos pos = CprDecoder.decodePosition(0.62,0.42,0.6200000000000000001,0.4200000000000000001,0);
        assertEquals(-2.3186440486460924, Units.convert((pos.longitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        assertEquals(2.5199999939650297, Units.convert((pos.latitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
    }
    @Test
    public void testEdd()
    {   GeoPos a = CprDecoder.decodePosition(0.3,0.3,0.3,0.3,0);
        GeoPos b = CprDecoder.decodePosition(0.3, 0.3, 0.3, 0.3, 1);
        GeoPos decodedPosition = CprDecoder.decodePosition(0.3,0.3,0.3,0.3,1);
        assertEquals(1.862068958580494, Units.convert((decodedPosition.longitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        assertEquals(1.8305084947496653, Units.convert((decodedPosition.latitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        GeoPos decodedPosition0 = CprDecoder.decodePosition(0.3,0.3,0.3,0.3,0);
        assertEquals(1.8305084947496653, Units.convert((decodedPosition0.longitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        assertEquals(1.7999999597668648, Units.convert((decodedPosition0.latitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        assertNull(CprDecoder.decodePosition(0, 0.3, 0, 0, 0));
    }
    @Test
    public void testChangeOfInvalidLatitude()
    {
        double x0 = Math.scalb(111600,-17);
        double x1 = Math.scalb(108865,-17);
        double y0 = 0.999999;
        double y1 = 0.439203;
        int mostRecent = 0;
        GeoPos decodedPosition = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertEquals(null,decodedPosition);
    }
    @Test
    public void testDecodePosition()
    {   double x0 = Math.scalb(111600,-17);
        double x1 = Math.scalb(108865,-17);
        double y0 = Math.scalb(94445,-17);
        double y1 = Math.scalb(77558,-17);
        int mostRecent = 0;
        GeoPos decodedPosition = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertEquals(7.476062346249819, Units.convert((decodedPosition.longitudeT32()),Units.Angle.T32,                Units.Angle.DEGREE));
        assertEquals(46.323349038138986, Units.convert((decodedPosition.latitudeT32()),Units.Angle.T32,                Units.Angle.DEGREE));
    }

    private final static double DIVIDER = Math.scalb(1.0, 17);
    @Test
    void testDecodePositionp()
    {
        double x0 = 111600 / DIVIDER;
        double y0 = 94445 / DIVIDER;
        double x1 = 108865 / DIVIDER;
        double y1 = 77558 / DIVIDER;
        double expectedLat0 = Units.convertFrom((1.0 / 60) * (7 + y0), Units.Angle.TURN);
        double expectedLat1 = Units.convertFrom((1.0 / 59) * (7 + y1), Units.Angle.TURN);
        double expectedLon0 = Units.convertFrom((1.0 / 41) * x0, Units.Angle.TURN);
        double expectedLon1 = Units.convertFrom((1.0 / 40) * x1, Units.Angle.TURN);
        int mostRecent = 0;
        GeoPos pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(expectedLat0, pos.latitude(), 1e-9);
        assertEquals(expectedLon0, pos.longitude(), 1e-9);
        mostRecent = 1;
        pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(expectedLat1, pos.latitude(), 1e-9);
        assertEquals(expectedLon1, pos.longitude(), 1e-9);
        mostRecent = 2;
        int finalMostRecent = mostRecent;
        assertThrows(IllegalArgumentException.class, () ->
        {CprDecoder.decodePosition(x0, y0, x1, y1, finalMostRecent);});
    }
    @Test
    public void testImpairLongitudeZones()
    {
        double lat = 37.0;
        double lon = -120.0;
        GeoPos cprResult = CprDecoder.decodePosition(lat, lon,lat,lat,1);
        assertNull(cprResult);
    }
    @Test
    public void testDecodePositions()
    {
        double x0 = Math.scalb(111600,-17);
        double x1 = Math.scalb(108865,-17);
        double y0 = Math.scalb(94445,-17);
        double y1 = Math.scalb(77558,-17);
        int mostRecent = 0;
        GeoPos decodedPosition = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertEquals(7.476062, Units.convert((decodedPosition.longitudeT32()),Units.Angle.T32, Units.Angle.DEGREE),1e-6);
        assertEquals(46.323349, Units.convert((decodedPosition.latitudeT32()),Units.Angle.T32, Units.Angle.DEGREE),1e-6);
    }
    @Test
    void testDecodePosition2()
    {
        double x0 = 0.851440;
        double x1 = 0.830574;
        double y0 = 0.720558;
        double y1 = 0.591721;
        int mostRecent = 0;
        GeoPos pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(Units.convertFrom(46.323349,Units.Angle.DEGREE), pos.latitude(), 1e-7);
        assertEquals(Units.convertFrom(7.476062,Units.Angle.DEGREE), pos.longitude(), 1e-7);
        mostRecent = 1;
        pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(Units.convertFrom(46.322363,Units.Angle.DEGREE), pos.latitude(), 1e-7);
        assertEquals(Units.convertFrom(7.475166,Units.Angle.DEGREE), pos.longitude(), 1e-7);
        mostRecent = 2;
        int finalMostRecent = mostRecent;
        assertThrows(IllegalArgumentException.class, () -> {CprDecoder.decodePosition(x0, y0, x1, y1, finalMostRecent);});
    }
    @Test
    public void ArgumentTest()
    {
        assertThrows(IllegalArgumentException.class,()-> CprDecoder.decodePosition(0,0,0,0,5));
    }
    @Test
    public void ArgumentTest2()
    {
        assertEquals(null, CprDecoder.decodePosition(-5,-5,0,0,0));
        assertEquals(null, CprDecoder.decodePosition(0,0,-5,-5,0));
        assertEquals(null, CprDecoder.decodePosition(0,0,0,-5,0));
    }
    @Test
    public void ArgumentTest4()
    {
        GeoPos pos = CprDecoder.decodePosition(-5,1,0,0,0);
        assertEquals(-180, Units.convert((pos.longitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        assertEquals(0, Units.convert((pos.latitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        GeoPos pos1 = CprDecoder.decodePosition(0,0,0,0,0);
        assertEquals(0, Units.convert((pos1.longitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
        assertEquals(0, Units.convert((pos1.latitudeT32()),Units.Angle.T32, Units.Angle.DEGREE));
    }
    @Test
    public void ArgumentTest3()
    {
        System.out.println(CprDecoder.decodePosition(0.26,1.51,1.25,2.36,0));
    }
}