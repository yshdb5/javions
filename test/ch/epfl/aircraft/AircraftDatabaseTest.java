package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        AircraftDatabase dataBaseTest = new AircraftDatabase("/aircraft.zip");

        AircraftData actualResults = dataBaseTest.get(new IcaoAddress("141C80"));

        AircraftData expectedResults = new AircraftData(new AircraftRegistration("RA-07296"), new AircraftTypeDesignator("AS55"),
                "AEROSPATIALE AS-355 Ecureuil 2", new AircraftDescription("H2T"), WakeTurbulenceCategory.of("L"));

        assertEquals(expectedResults, actualResults);
    }

}