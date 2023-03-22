package ch.epfl.adsb;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        22/03/23
 */

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;

public class AircraftState implements AircraftStateSetter
{

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs)
    {
        System.out.println("time stamp : " + timeStampNs);
    }

    @Override
    public void setCategory(int category)
    {
        System.out.println("catégorie : " + category);
    }

    @Override
    public void setCallSign(CallSign callSign)
    {
        System.out.println("indicatif : " + callSign);
    }

    @Override
    public void setPosition(GeoPos position)
    {
        System.out.println("position : " + position);
    }

    @Override
    public void setAltitude(double altitude)
    {
        System.out.println("altitude : " + altitude);
    }

    @Override
    public void setVelocity(double velocity)
    {
        System.out.println("vélocité : " + velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading)
    {
        System.out.println("Track or heading : " + trackOrHeading);
    }
}
