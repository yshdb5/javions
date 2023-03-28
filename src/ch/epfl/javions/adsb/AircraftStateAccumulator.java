package ch.epfl.javions.adsb;


import ch.epfl.javions.GeoPos;

import java.util.Objects;

/**
 * class AircraftStateAccumulator : represents an object accumulating ADS-B messages from a single aircraft
 * to determine its status over time
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private T stateSetter;
    private AirbornePositionMessage lastEvenMessage;
    private AirbornePositionMessage lastOddMessage;

    /**
     * AircraftStateAccumulator's constructor
     * returns an aircraft state accumulator associated with the given modifiable state
     *
     * @param stateSetter a modifiable state
     * @throws NullPointerException if the modifiable state is null
     */
    public AircraftStateAccumulator(T stateSetter) {
        this.stateSetter = stateSetter;
        lastEvenMessage = null;
        lastOddMessage = null;
        Objects.requireNonNull(stateSetter);
    }

    /**
     * @return the modifiable state of the aircraft passed to its constructor
     */
    public T stateSetter() {
        return stateSetter;
    }

    /**
     * updates the modifiable state according to the given message
     *
     * @param message
     */
    public void update(Message message) {
        switch (message) {
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCallSign(aim.callSign());
                stateSetter.setCategory(aim.category());
            }
            case AirbornePositionMessage apm -> {
                stateSetter.setAltitude(apm.altitude());
                GeoPos position;

                if (apm.parity() == 0) {
                    if ((lastOddMessage != null) && validInterval(apm, lastOddMessage)) {
                        position = CprDecoder.decodePosition(apm.x(), apm.y(), lastOddMessage.x(), lastOddMessage.y(), apm.parity());
                        stateSetter.setPosition(position);
                    }
                    lastEvenMessage = apm;
                } else {
                    if ((lastEvenMessage != null) && validInterval(apm, lastEvenMessage)) {
                        position = CprDecoder.decodePosition(lastEvenMessage.x(), lastEvenMessage.y(), apm.x(), apm.y(), apm.parity());
                        stateSetter.setPosition(position);
                    }
                    lastOddMessage = apm;
                }
            }
            case AirborneVelocityMessage avm -> {
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }
            default -> throw new Error();
        }
    }

    private boolean validInterval(Message mess0, Message mess1) {
        return mess0.timeStampNs() - mess1.timeStampNs() <= Math.pow(10, 10);
    }
}
