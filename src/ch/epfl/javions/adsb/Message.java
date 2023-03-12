package ch.epfl.javions.adsb;


import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * interface Message : implemented by all classes representing " analysed " ADS-B messages
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public interface Message

{
    /**
     * @return the timestamp of the message, in nanoseconds
     */
    long timeStampNs();

    /**
     * @return the ICAO address of the sender of the message.
     */
    IcaoAddress icaoAddress();

}
