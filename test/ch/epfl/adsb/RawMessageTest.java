package ch.epfl.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RawMessageTest
{
    @Test
    void RawMessageThrowsIllegalArgumentException()
    {
        byte [] tab1 = {0, 1, 2};
        byte [] tab2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        int timeStamp1 = -2;
        int timeStamp2 =  0;
        ByteString byteString1 = new ByteString(tab1);
        ByteString byteString2 = new ByteString(tab2);

        assertThrows(IllegalArgumentException.class, ()-> new RawMessage(timeStamp2, byteString1));
        assertThrows(IllegalArgumentException.class, ()-> new RawMessage(timeStamp1, byteString2));
        assertDoesNotThrow(()-> new RawMessage(timeStamp2, byteString2));
    }

    @Test
    void SizeWorksOnKnownValues()
    {
        byte byte0 = (byte) Byte.toUnsignedInt((byte) 0b10001000);
        int actual = RawMessage.size(byte0);
        int expected =  17;
        assertEquals(expected, actual);

        byte byte1 = 0b01010100;
        actual = RawMessage.size(byte1);
        expected = 0;
        assertEquals(expected, actual);

        byte byte2 = 0b00000000;
        actual = RawMessage.size(byte2);
        assertEquals(expected, actual);

    }
}