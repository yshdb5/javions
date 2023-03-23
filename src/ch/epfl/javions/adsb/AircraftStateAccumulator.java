package ch.epfl.javions.adsb;
/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/03/23
 */

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

import java.util.Objects;

public class AircraftStateAccumulator <T extends AircraftStateSetter>
{
    private T stateSetter;
    private AirbornePositionMessage lastEvenMessage;
    private AirbornePositionMessage lastOddMessage;

    public AircraftStateAccumulator(T stateSetter)
    {
        this.stateSetter = stateSetter;
        lastEvenMessage = null;
        lastOddMessage = null;
        Objects.requireNonNull(stateSetter);
    }

    public T stateSetter()
    {
        return stateSetter;
    }

    public void update(Message message)
    {
        switch (message)
        {
            case AircraftIdentificationMessage aim ->
            {
                stateSetter.setCallSign(aim.callSign());
                stateSetter.setCategory(aim.category());
            }
            case AirbornePositionMessage apm ->
            {
                stateSetter.setAltitude(apm.altitude());
                GeoPos position;

                if (apm.parity() == 0)
                {
                    if ((lastOddMessage != null) && validInterval(apm, lastOddMessage))
                    {
                        position = CprDecoder.decodePosition(apm.x(), apm.y(), lastOddMessage.x(), lastOddMessage.y(), apm.parity());
                        stateSetter.setPosition(position);
                    }
                    lastEvenMessage = apm;
                }
                else if (apm.parity() == 1)
                {
                    if ((lastEvenMessage != null) && validInterval(apm, lastEvenMessage))
                    {
                        position = CprDecoder.decodePosition(lastEvenMessage.x(), lastEvenMessage.y(), apm.x(), apm.y(), apm.parity());
                        stateSetter.setPosition(position);
                    }
                    lastOddMessage = apm;
                }
            }
            case AirborneVelocityMessage avm ->
            {
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }
            default -> throw new Error();
        }
    }

    private boolean validInterval (Message mess0, Message mess1)
    {
        return mess0.timeStampNs() - mess1.timeStampNs() <= Math.pow(10, 10);
    }
}
