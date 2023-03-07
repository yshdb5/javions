package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationTest {

    @Test
    void AircraftRegistrationThrowsIllegalArgumentsException()
    {

        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("HB*JDC"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("jszj"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("\\"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
    }

    @Test
    void AircraftRegistrationDoesNotThrowIllegalArgumentsException()
    {
        assertDoesNotThrow(() -> new AircraftRegistration("HB-JDC"));
        assertDoesNotThrow(() -> new AircraftRegistration("HB_.+-/?AAAAAAA"));
    }

    @Test
    void aircraftRegistrationConstructorThrowsWithInvalidRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("abc");
        });
    }

    @Test
    void aircraftRegistrationConstructorThrowsWithEmptyRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("");
        });
    }

    @Test
    void aircraftRegistrationConstructorAcceptsValidRegistration() {
        assertDoesNotThrow(() -> {
            new AircraftRegistration("F-HZUK");
        });
    }
}