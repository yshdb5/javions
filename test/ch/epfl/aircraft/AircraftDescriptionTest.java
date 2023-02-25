package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDescriptionTest {

    @Test
    void  AircraftDescriptionThrowsIllegalArgumentsException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("Z2J"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L9J"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L$J"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L2A"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L2"));
    }

    @Test
    void  AircraftDescriptionDoesNotThrowIllegalArgumentsException()
    {
        assertDoesNotThrow(() -> new AircraftDescription("L2J"));
        assertDoesNotThrow(() -> new AircraftDescription("-2-"));
        assertDoesNotThrow(() -> new AircraftDescription("L4T"));
        assertDoesNotThrow(() -> new AircraftDescription("R6E"));
    }
}