package ch.epfl.javions.adsb;
/**
 * class MessageParser : transforms raw ADS-B messages into one of three types of messages:
 *                       identification, flight position, flight speed
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public class MessageParser
{
    private MessageParser(){};

    /**
     * @param rawMessage
     * @return the instance of AircraftIdentificationMessage, AirbornePositionMessage or AirborneVelocityMessage corresponding to the given raw message, or null if the type code of the latter does
     *            not correspond to any of these three message types, or if it is invalid.
     */
    public static Message parse(RawMessage rawMessage)
    {
        int typeCode =  rawMessage.typeCode();

        if (typeCode >= 1 && typeCode <= 4)
        {
            return AircraftIdentificationMessage.of(rawMessage);
        }
        else if ((typeCode >= 9 && typeCode <= 18) || (typeCode >= 20 && typeCode <= 22))
        {
            return AirbornePositionMessage.of(rawMessage);
        }
        else if (typeCode == 19)
        {
            return AirborneVelocityMessage.of(rawMessage);
        }
        else
        {
            return null;
        }
    }
}
