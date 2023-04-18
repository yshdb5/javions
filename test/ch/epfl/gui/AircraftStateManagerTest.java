package ch.epfl.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.AircraftStateManager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

class AircraftStateManagerTest
{
    public static void main(String[] args) {

        AircraftStateManager manager = new AircraftStateManager(new AircraftDatabase("resources/aircraft.zip"));

        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                manager.updateWithMessage(MessageParser.parse(RawMessage.of(timeStampNs, bytes)));
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }
        } catch (IOException e) { /* nothing to do */ }
    }
}