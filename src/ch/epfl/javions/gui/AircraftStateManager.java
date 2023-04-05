package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashMap;
import java.util.Map;

public final class AircraftStateManager {
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

    public void updateWithMessage(Message message)
    {
        lastMessage = message;

    }

    public void purge()
    {

    }
}
