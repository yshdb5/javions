package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * record AircraftIdentificationMessage : represents an ADS-B message of identification and category
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */


public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {
    private final static int CA_START = 48;
    private final static int CA_LENGTH = 3;
    private final static int CHARS_START = 42;
    private final static int CHARS_NUMBER = 8;
    private final static int CHARS_SIZE = 6;
    private final static int LETTERS_BOUND1 = 1;
    private final static int LETTERS_BOUND2 = 26;
    private final static int LETTERS_START_ASCII = 64;
    private final static int NUMBERS_BOUND1 = 48;
    private final static int NUMBERS_BOUND2 = 57;
    private final static int SPACE_CHAR = 32;
    private final static int GIVEN_CONST = 14;

    /**
     * @param timeStampNs the time stamp of the message, in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param category    the category of aircraft of the shipper
     * @param callSign    the sender's call sign
     * @throws NullPointerException if icaoAddress or callSign are nul
     * @throws NullPointerException if timeStampNs is strictly less than 0.
     */
    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * @param rawMessage the raw message that gives the identification message
     * @return the identification message corresponding to the given raw message,
     * or null if at least one of the characters of the code it contains is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();

        int categoryByte = Bits.extractUInt(rawMessage.payload(), CA_START, CA_LENGTH);

        String callString = extractCallstring(rawMessage.payload());

        if (callString == null) return null;

        while (callString.endsWith(" ")) {
            callString = callString.substring(0, (callString.length() - 1));
        }

        long timeStamps = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int category = Byte.toUnsignedInt((byte) (((GIVEN_CONST - typeCode) << 4) | categoryByte));

        CallSign callSign = new CallSign(callString);

        return new AircraftIdentificationMessage(timeStamps, icaoAddress, category, callSign);
    }

    private static String extractCallstring(long payload) {

        int bitStart = CHARS_START;
        StringBuilder callString = new StringBuilder();
        for (int i = 0; i < CHARS_NUMBER; i++) {
            int extractedInt = Bits.extractUInt(payload, bitStart, CHARS_SIZE);
            if (extractedInt >= LETTERS_BOUND1 && extractedInt <= LETTERS_BOUND2)
                callString.append((char) (extractedInt + LETTERS_START_ASCII));
            else if ((extractedInt >= NUMBERS_BOUND1 && extractedInt <= NUMBERS_BOUND2) || (extractedInt == SPACE_CHAR))
                callString.append((char) extractedInt);
            else return null;
            bitStart -= CHARS_SIZE;
        }
        return callString.toString();
    }
}
