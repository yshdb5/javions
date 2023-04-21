package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public final class ObservableAircraftState implements AircraftStateSetter {
    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private final LongProperty lastMessageTimeStampNs;
    private final IntegerProperty category;
    private final ObjectProperty<CallSign> callSign;
    private final ObjectProperty<GeoPos> position;
    private final DoubleProperty altitude;
    private final DoubleProperty velocity;
    private final DoubleProperty trackOrHeading;
    private final ObservableList<AirbornePos> trajectory;
    private final ObservableList<AirbornePos> unmodifiableTrajectory;
    private double lastPositionTimeStamp;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
        lastMessageTimeStampNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty();
        velocity = new SimpleDoubleProperty();
        trackOrHeading = new SimpleDoubleProperty();
        trajectory = FXCollections.observableArrayList();
        unmodifiableTrajectory = FXCollections.unmodifiableObservableList(trajectory);
        lastPositionTimeStamp = -1;
    }

    public ReadOnlyLongProperty lastMessageTimeStampNs() {
        return lastMessageTimeStampNs;
    }

    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    public ReadOnlyObjectProperty callSignProperty() {
        return callSign;
    }

    public ReadOnlyObjectProperty positionProperty() {
        return position;
    }

    public ReadOnlyListProperty trajectoryProperty() {
        return (ReadOnlyListProperty) unmodifiableTrajectory;
    }

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    public int getCategory() {
        return category.get();
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    public CallSign getCallSign() {
        return callSign.get();
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    public GeoPos getPosition() {
        return position.get();
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        updateTrajectory();
    }

    public double getAltitude() {
        return altitude.get();
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    public double getVelocity() {
        return velocity.get();
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    public List<AirbornePos> getTrajectory() {
        return trajectory;
    }

    private void updateTrajectory() {
        double actualAltitude = getAltitude();
        GeoPos actualPos = getPosition();
        double lastTimeStamp = getLastMessageTimeStampNs();

        if (trajectory.isEmpty() || !trajectory.get(trajectory.size() - 1).pos.equals(actualPos)) {
            trajectory.add(new AirbornePos(actualPos, actualAltitude));
            lastPositionTimeStamp = lastTimeStamp;
        } else if (lastTimeStamp == lastPositionTimeStamp)
            trajectory.set(trajectory.size() - 1, new AirbornePos(actualPos, actualAltitude));
    }

    private record AirbornePos(GeoPos pos, double altitude) {
    }
}
