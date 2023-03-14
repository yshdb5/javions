package ch.epfl.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.IcaoAddress;
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
        byte byte0 = (byte) 0b010001000;
        int actual = RawMessage.size(byte0);
        int expected = RawMessage.LENGTH;
        assertEquals(expected, actual);

        byte byte1 = 0b01010100;
        actual = RawMessage.size(byte1);
        expected = 0;
        assertEquals(expected, actual);

        byte byte2 = 0b00000000;
        actual = RawMessage.size(byte2);
        assertEquals(expected, actual);
    }

    @Test
    void TypeCodeWorksOnKnwonValues()
    {
        int expected = RawMessage.typeCode(1);
        int actual = 0;
    }

    @Test
    void TypeCode2WorksOnKnwonValues()
    {


    }

    @Test
    void DownLinkFormatWorksOnKnwonValues()
    {

    }

    @Test
    void IcaoAddressWorksOnKnownValues()
    {

    }

    @Test
    void PayloadWorksOnKnwonValues()
    {

    }


}