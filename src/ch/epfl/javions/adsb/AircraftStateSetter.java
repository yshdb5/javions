package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * Interface AircraftStateSetter : implemented by all classes representing the (changeable) state of an aircraft.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public interface AircraftStateSetter {
    /**
     * Changes the timestamp of the last message received from the aircraft to the given value.
     *
     * @param timeStampNs the new time stamp in nanoseconds
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Changes the category of the aircraft to the given value.
     *
     * @param category the new category
     */
    void setCategory(int category);

    /**
     * Which changes the aircraft designator to the given value.
     *
     * @param callSign the new aircraft designator value
     */
    void setCallSign(CallSign callSign);

    /**
     * Which changes the position of the aircraft to the given value.
     *
     * @param position the new position of the aircraft
     */
    void setPosition(GeoPos position);

    /**
     * Which changes the altitude of the aircraft to the given value.
     *
     * @param altitude the new altitude of the aircraft
     */
    void setAltitude(double altitude);

    /**
     * Which changes the speed of the aircraft to the given value.
     *
     * @param velocity the new speed of the aircraft
     */
    void setVelocity(double velocity);

    /**
     * Changes the direction of the aircraft to the given value.
     *
     * @param trackOrHeading the new direction of the aircraft
     */
    void setTrackOrHeading(double trackOrHeading);
}
