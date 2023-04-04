package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ObservableAircraftState implements AircraftStateSetter {
    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private LongProperty lastMessageTimeStampNs;
    private IntegerProperty category;
    private ObjectProperty<CallSign> callSign;
    private ObjectProperty<GeoPos> position;
    private DoubleProperty altitude;
    private DoubleProperty velocity;
    private DoubleProperty trackOrHeading;
    private ObservableList<AirbornePos> modifiableList;
    private ObservableList<AirbornePos> unmodifiableList;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData)
    {
        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
        lastMessageTimeStampNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty();
        velocity = new SimpleDoubleProperty();
        trackOrHeading = new SimpleDoubleProperty();
        modifiableList = FXCollections.observableArrayList();
        unmodifiableList = FXCollections.unmodifiableObservableList(modifiableList);
    }

    private record AirbornePos(GeoPos pos, int altitude) {}

    public ReadOnlyLongProperty lastMessageTimeStampNs()
    {
        return lastMessageTimeStampNs;
    }
    public ReadOnlyIntegerProperty categoryProperty()
    {
        return category;
    }

    public ReadOnlyObjectProperty callSignProperty()
    {
        return callSign;
    }

    public ReadOnlyObjectProperty positionProperty()
    {
        return position;
    }

    public ReadOnlyListProperty trajectoryProperty()
    {
        return (ReadOnlyListProperty) unmodifiableList;
    }

    public ReadOnlyDoubleProperty altitudeProperty()
    {
        return altitude;
    }

    public ReadOnlyDoubleProperty velocityProperty()
    {
        return velocity;
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty()
    {
        return trackOrHeading;
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    public void setTrajectory(double trajectory)
    {

    }
}
