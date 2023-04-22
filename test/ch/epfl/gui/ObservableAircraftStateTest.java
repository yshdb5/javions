package ch.epfl.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

class ObservableAircraftStateTest
{
    @Test
    void Test()
    {
        String d = getClass().getResource("/messages_20230318_0915.bin").getFile();
        d = URLDecoder.decode(d, UTF_8);

        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(d)))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}