package ch.epfl.javions;

import ch.epfl.javions.Crc24;
import org.junit.jupiter.api.Test;
import java.util.HexFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Crc24Test {

    @Test
    void crcWorksOnKnownValues1()
    {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D392AE499107FB5C00439";
        String cS = "035DB8";
        int c = Integer.parseInt(cS, 16); // == 0x035DB8

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

    @Test
    void crcWorksOnKnownValues2()
    {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D4D2286EA428867291C08";
        String cS = "EE2EC6";
        int c = Integer.parseInt(cS, 16); // == 0xEE2EC6

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }
    @Test
    void crcWorkOnEmptyTab()
    {
    }

    @Test
    void crcWorkOnTabWithSingleValue()
    {
    }

}