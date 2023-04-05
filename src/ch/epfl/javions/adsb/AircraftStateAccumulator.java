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
    private static final long MAX_TIME_INTERVAL_NS = (long) Math.pow(10, 10);
    private final T stateSetter;
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
        Objects.requireNonNull(stateSetter);
        this.stateSetter = stateSetter;
        lastEvenMessage = null;
        lastOddMessage = null;
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
     * @param message that allows to modify the aircraft state
     */
    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCallSign(aim.callSign());
                stateSetter.setCategory(aim.category());
            }
            case AirbornePositionMessage apm -> {
                stateSetter.setAltitude(apm.altitude());

                if (apm.parity() == 0) {
                    setPosition(lastOddMessage, apm);
                    lastEvenMessage = apm;
                } else {
                    setPosition(lastEvenMessage, apm);
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
        return (mess0.timeStampNs() - mess1.timeStampNs()) <= MAX_TIME_INTERVAL_NS;
    }

    private void setPosition(AirbornePositionMessage lastMessage, AirbornePositionMessage apm) {
        GeoPos position;
        if ((lastMessage != null) && validInterval(apm, lastMessage)) {
            position = (apm.parity() == 0) ?
                    CprDecoder.decodePosition(apm.x(), apm.y(), lastMessage.x(), lastMessage.y(), apm.parity()) :
                    CprDecoder.decodePosition(lastMessage.x(), lastMessage.y(), apm.x(), apm.y(), apm.parity());
            if (position != null) stateSetter.setPosition(position);
        }
    }
}
