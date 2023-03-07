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

    private AircraftDatabase getDatabase() {
        // Try to get the database from the resources
        var aircraftResourceUrl = getClass().getResource("/aircraft.zip");
        if (aircraftResourceUrl != null)
            return new AircraftDatabase(URLDecoder.decode(aircraftResourceUrl.getFile(), UTF_8));

        // Try to get the database from the JAVIONS_AIRCRAFT_DATABASE environment variable
        // (only meant to simplify testing of several projects with a single database)
        var aircraftFileName = System.getenv("JAVIONS_AIRCRAFT_DATABASE");
        if (aircraftFileName != null)
            return new AircraftDatabase(aircraftFileName);

        throw new Error("Could not find aircraft database");
    }

    @Test
    void aircraftDatabaseGetReturnsNullWhenAddressDoesNotExist() throws IOException {
        var aircraftDatabase = getDatabase();
        assertNull(aircraftDatabase.get(new IcaoAddress("123456")));
    }

    @Test
    void aircraftDatabaseGetWorksWithFirstLineOfFile() throws IOException {
        var aircraftDatabase = getDatabase();
        var aircraftData = aircraftDatabase.get(new IcaoAddress("0086AB"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("ZS-CNA"), aircraftData.registration());
    }

    @Test
    void aircraftDatabaseGetWorksWithLastLineOfFile() throws IOException {
        var aircraftDatabase = getDatabase();
        var aircraftData = aircraftDatabase.get(new IcaoAddress("E808C0"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("CC-DAW"), aircraftData.registration());
    }

    @Test
    void aircraftDatabaseGetWorksWithAddressGreaterThanLastOneOfFile() throws IOException {
        var aircraftDatabase = getDatabase();
        var aircraftData = aircraftDatabase.get(new IcaoAddress("FFFF01"));
        assertNull(aircraftData);
    }

    @Test
    void aircraftDatabaseGetReturnsCorrectData() throws IOException {
        var aircraftDatabase = getDatabase();
        var aircraftData = aircraftDatabase.get(new IcaoAddress("4B1805"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("HB-JCN"), aircraftData.registration());
        assertEquals(new AircraftTypeDesignator("BCS3"), aircraftData.typeDesignator());
        assertEquals("AIRBUS A220-300", aircraftData.model());
        assertEquals(new AircraftDescription("L2J"), aircraftData.description());
        assertEquals(WakeTurbulenceCategory.MEDIUM, aircraftData.wakeTurbulenceCategory());
    }

    //    @Test
    void aircraftDatabaseGetWorksWithEmptyColumns() throws IOException {
        var aircraftDatabase = getDatabase();
        var aircraftData = aircraftDatabase.get(new IcaoAddress("AAAAAA"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("N787BK"), aircraftData.registration());
        assertEquals(new AircraftTypeDesignator(""), aircraftData.typeDesignator());
        assertEquals("", aircraftData.model());
        assertEquals(new AircraftDescription(""), aircraftData.description());
        assertEquals(WakeTurbulenceCategory.UNKNOWN, aircraftData.wakeTurbulenceCategory());
    }

}