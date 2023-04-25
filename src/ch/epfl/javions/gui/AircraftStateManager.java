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

public final class AircraftStateManager {
    private static final long MAX_TIME_INTERVAL_NS = Duration.ofMinutes(1).toNanos();
    private final AircraftDatabase database;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> accumulatorMap;
    private final ObservableSet<ObservableAircraftState> statesAccumulatorList;
    private final ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList;
    private Message lastMessage;

    public AircraftStateManager(AircraftDatabase database) {
        this.database = database;
        accumulatorMap = new HashMap<>();
        statesAccumulatorList = FXCollections.observableSet();
        unmodifiableStatesAccumulatorList = FXCollections.unmodifiableObservableSet(statesAccumulatorList);
        lastMessage = null;
    }

    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableStatesAccumulatorList;
    }

    public void updateWithMessage(Message message) throws IOException {
        ObservableAircraftState observableAircraftState =
                new ObservableAircraftState(message.icaoAddress(), database.get(message.icaoAddress()));

        accumulatorMap.putIfAbsent(message.icaoAddress(),
                new AircraftStateAccumulator<>(observableAircraftState));
        accumulatorMap.get(message.icaoAddress()).update(message);

        statesAccumulatorList.add(observableAircraftState);
        lastMessage = message;
    }

    public void purge() {
        statesAccumulatorList.removeIf(state ->
                (lastMessage.timeStampNs() - state.getLastMessageTimeStampNs()) > MAX_TIME_INTERVAL_NS);
    }
}

