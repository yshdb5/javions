package ch.epfl.adsb;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallSignTest {

    @Test
    void CallSignThrowsIllegalArgumentsException()
    {
        assertThrows(IllegalArgumentException.class, () -> new CallSign("A20NNNNNNNNN"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("A2*NN"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("qdcscd"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("\\"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("-"));
    }

    @Test
    void CallSignDoesNotThrowIllegalArgumentsException()
    {
        assertDoesNotThrow(() -> new CallSign("A20N"));
        assertDoesNotThrow(() -> new CallSign("A"));
        assertDoesNotThrow(() -> new CallSign("0"));
        assertDoesNotThrow(() -> new CallSign("A20PPSPS"));
        assertDoesNotThrow(() -> new CallSign(""));
    }
}