package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class AircraftStateManager {
    private static final long MAX_TIME_INTERVAL_NS = Duration.ofMinutes(1).toNanos();
    private final AircraftDatabase database;
    private Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> accumulatorMap;
    private ObservableSet<ObservableAircraftState> stateAccumulatorList;
    private ObservableSet<ObservableAircraftState> unmodifiablestateAccumulatorList;
    private Message lastMessage;

    public AircraftStateManager (AircraftDatabase database)
    {
        this.database = database;
        accumulatorMap = new HashMap<>();
        stateAccumulatorList = FXCollections.observableSet();
        unmodifiablestateAccumulatorList = FXCollections.unmodifiableObservableSet(stateAccumulatorList);
        lastMessage = null;
    }

    public ReadOnlyListProperty states()
    {
        return (ReadOnlyListProperty) unmodifiablestateAccumulatorList;
    }

    public void updateWithMessage(Message message) throws IOException {
        ObservableAircraftState observableAircraftState =
                new ObservableAircraftState(message.icaoAddress(), database.get(message.icaoAddress()));

        accumulatorMap.putIfAbsent(message.icaoAddress(),
                new AircraftStateAccumulator<>(observableAircraftState));
        accumulatorMap.get(message.icaoAddress()).update(message);

        stateAccumulatorList.add(observableAircraftState);
        lastMessage = message;
    }

    public void purge()
    {
        stateAccumulatorList.removeIf(state ->
                (lastMessage.timeStampNs() - state.getLastMessageTimeStampNs()) > MAX_TIME_INTERVAL_NS);
    }
}
