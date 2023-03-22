package ch.epfl.javions.adsb;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/03/23
 */

public class MessageParser
{
    private MessageParser(){};

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
