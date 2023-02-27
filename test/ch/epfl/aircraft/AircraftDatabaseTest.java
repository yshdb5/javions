package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.AircraftDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDatabaseTest
{

    @Test
    void AircraftDatabaseThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () -> new AircraftDatabase(null));
    }
}