package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftTypeDesignatorTest {

    @Test
    void AircraftTypeDesignatorThrowsIllegalArgumentsException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A20NN"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A2*NN"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("2"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("\\"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("aaaa"));
    }

    @Test
    void AircraftTypeDesignatorDoesNotThrowIllegalArgumentsException()
    {
        assertDoesNotThrow(() -> new AircraftTypeDesignator("A20N"));
        assertDoesNotThrow(() -> new AircraftTypeDesignator("A2"));
        assertDoesNotThrow(() -> new AircraftTypeDesignator("A20"));
        assertDoesNotThrow(() -> new AircraftTypeDesignator(""));
    }

    @Test
    void aircraftTypeDesignatorConstructorThrowsWithInvalidTypeDesignator() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftTypeDesignator("ABCDE");
        });
    }

    @Test
    void aircraftTypeDesignatorConstructorAcceptsEmptyTypeDesignator() {
        assertDoesNotThrow(() -> {
            new AircraftTypeDesignator("");
        });
    }

    @Test
    void aircraftTypeDesignatorConstructorAcceptsValidTypeDesignator() {
        assertDoesNotThrow(() -> {
            new AircraftTypeDesignator("BCS3");
        });
    }
}