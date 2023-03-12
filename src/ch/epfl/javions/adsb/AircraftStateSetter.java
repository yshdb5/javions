package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * interface AircraftStateSetter : implemented by all classes representing the (changeable) state of an aircraft
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public interface AircraftStateSetter

{
    /**
     * changes the timestamp of the last message received from the aircraft to the given value
     * @param timeStampNs
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     *
     changes the category of the aircraft to the given value
     * @param category
     */
    void setCategory(int category);

    /**
     * which changes the aircraft designator to the given value
     * @param callSign
     */
    void setCallSign(CallSign callSign);

    /**
     * which changes the position of the aircraft to the given value
     * @param position
     */
    void setPosition(GeoPos position);

    /**
     * which changes the altitude of the aircraft to the given value
     * @param altitude
     */
    void setAltitude(double altitude);

    /**
     * which changes the speed of the aircraft to the given value,
     * @param velocity
     */
    void setVelocity(double velocity);

    /**
     * changes the direction of the aircraft to the given value.
     * @param trackOrHeading
     */
    void setTrackOrHeading(double trackOrHeading);
}
