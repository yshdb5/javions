package ch.epfl.aircraft;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IcaoAdressTest {

    @Test
    void IcaoAdressThrowsIllegalArgumentsException()
    {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4b1814"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B18144"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B181"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4Z18144"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("suspicious"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
    }

    @Test
    void IcaoAdressDoesNotThrowIllegalArgumentsException()
    {
        assertDoesNotThrow(() -> new IcaoAddress("4B1814"));
        assertDoesNotThrow(() -> new IcaoAddress("999999"));
        assertDoesNotThrow(() -> new IcaoAddress("0FABD8"));
    }

    @Test
    void icaoAddressConstructorThrowsWithInvalidAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress("00000a");
        });
    }

    @Test
    void icaoAddressConstructorThrowsWithEmptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress("");
        });
    }

    @Test
    void icaoAddressConstructorAcceptsValidAddress() {
        assertDoesNotThrow(() -> {
            new IcaoAddress("ABCDEF");
        });
    }
}