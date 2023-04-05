package ch.epfl.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ObservableAircraftStateTest
{
    public static void main(String[] args) throws FileNotFoundException
    {
        printAllMessages();
    }
   static void printAllMessages() throws FileNotFoundException
   {
       try (DataInputStream s = new DataInputStream(
               new BufferedInputStream(
                       new FileInputStream("messages_20230318_0915.bin")))){
           byte[] bytes = new byte[RawMessage.LENGTH];
           while (true) {
               long timeStampNs = s.readLong();
               int bytesRead = s.readNBytes(bytes, 0, bytes.length);
               assert bytesRead == RawMessage.LENGTH;
               ByteString message = new ByteString(bytes);
               System.out.printf("%13d: %s\n", timeStampNs, message);
           }
       } catch (IOException e) { /* nothing to do */ }
   }
}