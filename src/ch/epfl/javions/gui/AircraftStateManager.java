package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Final AircraftStateManager class : is intended to keep the states of a set
 * of aircraft up to date based on messages received from them.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class AircraftStateManager {
    private static final long MAX_TIME_INTERVAL_NS = Duration.ofMinutes(1).toNanos();
    private final AircraftDatabase database;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> accumulatorMap;
    private final ObservableSet<ObservableAircraftState> statesAccumulatorList;
    private final ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList;
    private Message lastMessage;


    /**
     * AircraftStateManager's constructor.
     *
     * @param database the database containing the fixed characteristics of aircraft.
     */
    public AircraftStateManager(AircraftDatabase database) {
        this.database = database;
        accumulatorMap = new HashMap<>();
        statesAccumulatorList = FXCollections.observableSet();
        unmodifiableStatesAccumulatorList = FXCollections.unmodifiableObservableSet(statesAccumulatorList);
        lastMessage = null;
    }

    /**
     * @return the observable, but not modifiable, set of observable states of the aircraft whose position is known.
     */
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableStatesAccumulatorList;
    }


    /**
     * Takes a message to update the state of the aircraft that sent it.
     *
     * @param message the message we want to update the state of the aircraft
     * @throws IOException in case of input/output error.
     */
    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress address = message.icaoAddress();

        accumulatorMap.putIfAbsent(address, new AircraftStateAccumulator<>(
                new ObservableAircraftState(address, database.get(address))));
        accumulatorMap.get(address).update(message);

        ObservableAircraftState aircraftState = accumulatorMap.get(address).stateSetter();
        if (aircraftState.getPosition() != null)
            statesAccumulatorList.add(aircraftState);

        lastMessage = message;
    }
    /**
     * Removes from the set of observable states all those corresponding to aircraft
     * for which no message has been received in the minute preceding the reception
     * of the last message passed to updateWithMessage.
     */
    public void purge() {
        statesAccumulatorList.removeIf(state -> shouldRemove(state.getLastMessageTimeStampNs()));

        accumulatorMap.entrySet().removeIf(entry ->
                shouldRemove(entry.getValue().stateSetter().getLastMessageTimeStampNs()));
    }

    /**
     * @param lastTimeStampNs the time stamp of the last message received
     * @return true if the aircraft should be removed from the observable states and the accumulator map.
     */
    private boolean shouldRemove(long lastTimeStampNs) {
        return (lastMessage.timeStampNs() - lastTimeStampNs) > MAX_TIME_INTERVAL_NS;
    }
}

