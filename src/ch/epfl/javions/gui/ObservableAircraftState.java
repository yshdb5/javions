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

/**
 * final class ObservableAircraftState : represents the state of an aircraft.
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;

    private final AircraftData aircraftData;

    /** Contains the time stamp of the last message received from the aircraft, in nanoseconds.*/
    private final LongProperty lastMessageTimeStampNs;

    /** Contains the category of the aircraft.*/
    private final IntegerProperty category;

    /** Contains the call sign of the aircraft.*/
    private final ObjectProperty<CallSign> callSign;

    /** Contains the position of the aircraft on the surface of the Earth (longitude and latitude, in radians). */
    private final ObjectProperty<GeoPos> position;

    /** Contains the altitude of the aircraft, in meters. */
    private final DoubleProperty altitude;

    /** Contains the speed of the aircraft, in meters per second. */
    private final DoubleProperty velocity;

    /** Contains the track or heading of the aircraft, in radians.*/
    private final DoubleProperty trackOrHeading;

    /** Contains the trajectory of the aircraft. */
    private final ObservableList<AirbornePos> trajectory;

    /** An observable list not modifiable, of the positions in space that the aircraft has occupied since the first message received.*/
    private final ObservableList<AirbornePos> unmodifiableTrajectory;
    private double lastPositionTimeStamp;

    /**
     * ObservableAircraftState constructor
     * @param icaoAddress the ICAO address of the aircraft whose state is to be represented
     * @param aircraftData the fixed characteristics of the aircraft
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
        lastMessageTimeStampNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty();
        velocity = new SimpleDoubleProperty(Double.NaN);
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

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    public ReadOnlyListProperty<AirbornePos> trajectoryProperty() {
        return (ReadOnlyListProperty) unmodifiableTrajectory;
    }

    /**
     * Read-only altitude access method.
     * @return the altitude.
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * Read-only velocity access method.
     * @return the velocity.
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * Read-only trackOrHeading access method.
     * @return the trackOrHeading.
     */

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * Method to access the value contained in the icao address.
     * @return the value of the icao address.
     */

    public IcaoAddress getIcaoAddress() {return icaoAddress;}

    /**
     * Method to access the value contained in the aircraft data.
     * @return the value of the aircraft data.
     */

    public AircraftData getAircraftData() {return aircraftData;}

    /**
     * Method to access the value contained in the timestamp.
     * @return the value of the last message timestamp.
     */

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    /**
     * Modify the value contained in the time stamp.
     * @param timeStampNs the new time stamp in nanoseconds.
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    /**
     * Method to access the value contained in the aircraft's category.
     * @return the value of the aircraft's category.
     */

    public int getCategory() {
        return category.get();
    }

    /**
     * Modify the value contained in the category.
     * @param category the new category.
     */

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * Method to access the value contained in the aircraft's callsign.
     * @return the value of the aircraft's callsign.
     */

    public CallSign getCallSign() {return callSign.get();}

    /**
     * Modify the value contained in the callsign.
     * @param callSign the new aircraft designator value.
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /**
     * Method to access the value contained in the aircraft's position.
     * @return the value of the aircraft's position
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * Modify the value contained in the position.
     * @param position the new position of the aircraft.
     */
    @Override
    public void setPosition(GeoPos position) {
        if (position != null){
            this.position.set(position);
            updateTrajectory();
        }
    }

    /**
     * Method to access the value contained in the aircraft's altitude.
     * @return the value of the aircraft's altitude.
     */

    public double getAltitude() {
        return altitude.get();
    }

    /**
     * Modify the value contained in the altitude.
     * @param altitude the new altitude of the aircraft.
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    /**
     * Method to access the value contained in the aircraft's velocity.
     * @return the value of the aircraft's velocity.
     */

    public double getVelocity() {
        return velocity.get();
    }

    /**
     * Modify the value contained in the velocity.
     * @param velocity the new speed of the aircraft.
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    /**
     * Method to access the value contained in the aircraft's track or heading.
     * @return the value of the aircraft's track or heading.
     */

    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * Modify the value contained in the direction of the aircraft.
     * @param trackOrHeading the new direction of the aircraft.
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }


    /**
     * Method to access the value of the positions in space that the aircraft has occupied since the first message received.
     * @return the value the observable list of positions.
     */

    public ObservableList<AirbornePos> getTrajectory() {
        return unmodifiableTrajectory;
    }

    private void updateTrajectory() {
        double actualAltitude = getAltitude();
        if (getPosition() == null) return;
        GeoPos actualPos = getPosition();
        double lastTimeStamp = getLastMessageTimeStampNs();

        if (trajectory.isEmpty()
                        || trajectory.get(trajectory.size() - 1).pos.longitude() != actualPos.longitude()
                        || trajectory.get(trajectory.size() - 1).pos.latitude() != actualPos.latitude())
        {
            trajectory.add(new AirbornePos(actualPos, actualAltitude));
            lastPositionTimeStamp = lastTimeStamp;
        }
        else if (lastTimeStamp == lastPositionTimeStamp) {
            trajectory.set(trajectory.size() - 1, new AirbornePos(actualPos, actualAltitude));
        }
    }

    /**
     * Public record, AirbornePos, used to represent these positions in space.
     * @param pos
     * @param altitude
     */
    public record AirbornePos(GeoPos pos, double altitude) {}
}
