package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDataTest {

    @Test
    void AircraftDataThrowsNullPointerExceptionOnNullElements()
    {
        assertThrows(NullPointerException.class, () -> new AircraftData(null, null, null, null, null));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("HB-JDC"), null, null, null, null));
        assertThrows(NullPointerException.class, () -> new AircraftData(null, new AircraftTypeDesignator("A20N"), null, null, null));
        assertThrows(NullPointerException.class, () -> new AircraftData(null, null, "AIRBUS A-380", null, null));
        assertThrows(NullPointerException.class, () -> new AircraftData(null, null, null, new AircraftDescription("L2J"), null));
        assertThrows(NullPointerException.class, () -> new AircraftData(null, null, null, null, WakeTurbulenceCategory.HEAVY));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("HB-JDC"), new AircraftTypeDesignator("A20N"), "AIRBUS A-380", new AircraftDescription("L2J"), null));
    }

    @Test
    void AircraftDataDoesNotThrowNullPointerExceptionOnNonNullElements()
    {
        assertDoesNotThrow(() -> new AircraftData(new AircraftRegistration("HB-JDC"), new AircraftTypeDesignator("A20N"), "AIRBUS A-380", new AircraftDescription("L2J"), WakeTurbulenceCategory.HEAVY));
    }

    @Test
    void aircraftDataConstructorThrowsWithNullAttribute() {
        var registration = new AircraftRegistration("HB-JAV");
        var typeDesignator = new AircraftTypeDesignator("B738");
        var model = "Boeing 737-800";
        var description = new AircraftDescription("L2J");
        var wakeTurbulenceCategory = WakeTurbulenceCategory.LIGHT;
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(null, typeDesignator, model, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, null, model, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, null, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, null, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, null);
        });
    }
}