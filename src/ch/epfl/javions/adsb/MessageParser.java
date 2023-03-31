package ch.epfl.javions.adsb;

/**
 * class MessageParser : transforms raw ADS-B messages into one of three types of messages:
 * identification, flight position, flight speed
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public class MessageParser {
    private final static int IDENTIFICATION_BOUND1 = 1;
    private final static int IDENTIFICATION_BOUND2 = 4;
    private final static int POSITION_BOUND1 = 9;
    private final static int POSITION_BOUND2 = 18;
    private final static int POSITION_BOUND3 = 20;
    private final static int POSITION_BOUND4 = 22;
    private final static int VELOCITY_TYPECODE = 19;
    private MessageParser() {
    }

    /**
     * @param rawMessage
     * @return the instance of AircraftIdentificationMessage, AirbornePositionMessage or AirborneVelocityMessage corresponding to the given raw message, or null if the type code of the latter does
     * not correspond to any of these three message types, or if it is invalid.
     */
    public static Message parse(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();

        if (typeCode >= IDENTIFICATION_BOUND1 && typeCode <= IDENTIFICATION_BOUND2) {
            return AircraftIdentificationMessage.of(rawMessage);
        } else if ((typeCode >= POSITION_BOUND1 && typeCode <= POSITION_BOUND2) || (typeCode >= POSITION_BOUND3 && typeCode <= POSITION_BOUND4)) {
            return AirbornePositionMessage.of(rawMessage);
        } else if (typeCode == VELOCITY_TYPECODE) {
            return AirborneVelocityMessage.of(rawMessage);
        } else {
            return null;
        }
    }
}
