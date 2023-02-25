package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.IcaoAdress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IcaoAdressTest {

    @Test
    void IcaoAdressThrowsIllegalArgumentsException()
    {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAdress("4b1814"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAdress("4B18144"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAdress("4B181"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAdress("4Z18144"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAdress("suspicious"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAdress(""));
    }

    @Test
    void IcaoAdressDoesNotThrowIllegalArgumentsException()
    {
        assertDoesNotThrow(() -> new IcaoAdress("4B1814"));
        assertDoesNotThrow(() -> new IcaoAdress("999999"));
        assertDoesNotThrow(() -> new IcaoAdress("0FABD8"));
    }
}