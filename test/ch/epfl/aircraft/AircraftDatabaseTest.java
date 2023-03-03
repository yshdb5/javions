package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

class AircraftDatabaseTest
{

    @Test
    void AircraftDatabaseThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () -> new AircraftDatabase(null));
    }

    @Test
    void AircraftDatabaseReturnsKnownData() throws IOException
    {
        String dataBaseName = getClass().getResource("/aircraft.zip").getFile();
        dataBaseName = URLDecoder.decode(dataBaseName, UTF_8);

        AircraftDatabase dataBaseTest = new AircraftDatabase(dataBaseName);

        AircraftData actualResults = dataBaseTest.get(new IcaoAddress("141C80"));

        AircraftData expectedResults = new AircraftData(new AircraftRegistration("RA-07296"), new AircraftTypeDesignator("AS55"),
                "AEROSPATIALE AS-355 Ecureuil 2", new AircraftDescription("H2T"), WakeTurbulenceCategory.of("L"));

        assertEquals(expectedResults, actualResults);
    }

    @Test
    void AircraftDatabaseReturnsPartialData() throws IOException
    {
        String dataBaseName = getClass().getResource("/aircraft.zip").getFile();
        dataBaseName = URLDecoder.decode(dataBaseName, UTF_8);

        AircraftDatabase dataBaseTest = new AircraftDatabase(dataBaseName);

        AircraftData actualResults = dataBaseTest.get(new IcaoAddress("3EE418"));

        AircraftData expectedResults = new AircraftData(new AircraftRegistration("D-0897"), new AircraftTypeDesignator("GLID"),
                "", new AircraftDescription("L0-"), WakeTurbulenceCategory.of(""));

        assertEquals(expectedResults, actualResults);
    }

    @Test
    void AircraftDatabaseReturnsPartialData2() throws IOException
    {
        String dataBaseName = getClass().getResource("/aircraft.zip").getFile();
        dataBaseName = URLDecoder.decode(dataBaseName, UTF_8);

        AircraftDatabase dataBaseTest = new AircraftDatabase(dataBaseName);

        AircraftData actualResults = dataBaseTest.get(new IcaoAddress("A0FA18"));

        AircraftData expectedResults = new AircraftData(new AircraftRegistration("N162LB"), new AircraftTypeDesignator("BALL"),
                "", new AircraftDescription("B0-"), WakeTurbulenceCategory.of(""));

        assertEquals(expectedResults, actualResults);
    }

}