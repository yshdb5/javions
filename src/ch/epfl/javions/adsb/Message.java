package ch.epfl.javions.adsb;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        10/03/23
 */

import ch.epfl.javions.aircraft.IcaoAddress;

public interface Message
{
    long timeStampNs();

    IcaoAddress icaoAddress();
}
