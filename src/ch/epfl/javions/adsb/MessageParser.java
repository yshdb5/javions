package ch.epfl.javions.adsb;

/**
 * class MessageParser : transforms raw ADS-B messages into one of three types of messages:
 * identification, flight position, flight speed
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class MessageParser {

    private MessageParser() {
    }

    /**
     * @param rawMessage that gives the instance of AircraftIdentificationMessage, AirbornePositionMessage or AirborneVelocityMessage
     * @return the instance of AircraftIdentificationMessage, AirbornePositionMessage or AirborneVelocityMessage corresponding to the given raw message, or null if the type code of the latter does
     * not correspond to any of these three message types, or if it is invalid.
     */
    public static Message parse(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();

        return switch (typeCode)
        {
            case 1, 2, 3, 4 -> AircraftIdentificationMessage.of(rawMessage);
            case 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22 -> AirbornePositionMessage.of(rawMessage);
            case 19 -> AirborneVelocityMessage.of(rawMessage);
            default -> null;
        };
    }
}
