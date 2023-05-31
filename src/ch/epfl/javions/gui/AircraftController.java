package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Final  AircraftController class : manages the view of the aircraft.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class AircraftController {
    private static final int MAX_ALTITUDE = 12000;
    private static final double POWER_FACTOR = 1d / 3d;
    private static final int MAX_VISIBLE_ZOOM = 11;
    private static final int MIN_TRAJECTORIES = 2;
    private static final int OFFSET = 4;
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty;
    private final Pane pane;

    /**
     * AircraftController's constructor.
     *
     * @param mapParameters                     the parameters of the portion of the map visible on the screen.
     * @param unmodifiableStatesAccumulatorList the set (observable but not modifiable) of aircraft states
     *                                          that must appear on the view.
     * @param selectedAircraftStateProperty     a JavaFX property containing the state of the selected aircraft,
     *                                          whose content can be null when no aircraft is selected.
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList,
                              ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty) {
        this.mapParameters = mapParameters;
        this.unmodifiableStatesAccumulatorList = unmodifiableStatesAccumulatorList;
        this.selectedAircraftStateProperty = selectedAircraftStateProperty;
        this.pane = new Pane();

        pane.setPickOnBounds(false);
        pane.getStylesheets().add("/aircraft.css");

        setListeners();
    }

    /**
     * @return the JavaFX pane on which the aircraft are displayed
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Adds listeners to the set of aircraft states. Updates the view when states are added or removed.
     */
    private void setListeners() {
        unmodifiableStatesAccumulatorList.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        annotatedAircraft(change.getElementAdded());
                    }
                    if (change.wasRemoved())
                        pane.getChildren().removeIf(e ->
                                e.getId().equals(change.getElementRemoved().getIcaoAddress().string()));
                });
    }

    /**
     * Adds a new aircraft to the display.
     *
     * @param aircraftState the state of the aircraft to be added
     */
    private void annotatedAircraft(ObservableAircraftState aircraftState) {
        Group annotatedAircraftGroup = new Group(trajectory(aircraftState), iconLabel(aircraftState));

        annotatedAircraftGroup.setId(aircraftState.getIcaoAddress().string());
        annotatedAircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());

        pane.getChildren().add(annotatedAircraftGroup);
    }

    /**
     * Creates a group of graphical elements representing the aircraft's label and icon.
     *
     * @param aircraftState the state of the aircraft to represent
     * @return a group of graphical elements representing the aircraft
     */
    private Group iconLabel(ObservableAircraftState aircraftState) {
        Group iconLabelGroup = new Group(label(aircraftState), icon(aircraftState));

        iconLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.x(mapParameters.getZoom(), aircraftState.getPosition().longitude()) - mapParameters.getMinX(),
                aircraftState.positionProperty(), mapParameters.zoomProperty(), mapParameters.minXProperty()));

        iconLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.y(mapParameters.getZoom(), aircraftState.getPosition().latitude()) - mapParameters.getMinY(),
                aircraftState.positionProperty(), mapParameters.zoomProperty(), mapParameters.minYProperty()));

        return iconLabelGroup;
    }

    /**
     * Creates a trajectory Group for a given ObservableAircraftState.
     * The Group is positioned and made visible based on properties of the ObservableAircraftState
     * and the map parameters. The trajectory is redrawn when any of these properties change.
     *
     * @param aircraftState the ObservableAircraftState to create a trajectory for
     * @return a Group representing the aircraft's trajectory
     */
    private Group trajectory(ObservableAircraftState aircraftState) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");

        trajectoryGroup.visibleProperty().bind(Bindings.equal(aircraftState, selectedAircraftStateProperty));

        trajectoryGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> - mapParameters.getMinX(),
                mapParameters.minXProperty()));
        trajectoryGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> - mapParameters.getMinY(),
                mapParameters.minYProperty()));

        InvalidationListener redrawTrajectoryListener =
                l -> redrawTrajectory(aircraftState.getTrajectory(), trajectoryGroup);

        trajectoryGroup.visibleProperty().addListener((object, oldVisible, newVisible) ->
        {
            if (newVisible) {
                redrawTrajectory(aircraftState.getTrajectory(), trajectoryGroup);
                mapParameters.zoomProperty().addListener(redrawTrajectoryListener);
                aircraftState.getTrajectory().addListener(redrawTrajectoryListener);
            } else {
                trajectoryGroup.getChildren().clear();
                mapParameters.zoomProperty().removeListener(redrawTrajectoryListener);
                aircraftState.getTrajectory().removeListener(redrawTrajectoryListener);
            }
        });
        return trajectoryGroup;
    }

    /**
     * Redraws the trajectory of an aircraft within a specified Group.
     * The trajectory is represented by a series of lines drawn between the current and previous positions of the aircraft.
     *
     * @param trajectory      The observable list of positions for the aircraft
     * @param trajectoryGroup The group in which the trajectory is drawn
     */
    private void redrawTrajectory(ObservableList<ObservableAircraftState.AirbornePos> trajectory, Group trajectoryGroup) {
        if (trajectory.size() < MIN_TRAJECTORIES) return;
        trajectoryGroup.getChildren().clear();
        List<Line> lineList = new ArrayList<>();

        double previousX = 0;
        double previousY = 0;

        for (int i = 0; i < trajectory.size(); ++i) {
            Line line = new Line();

            double x = WebMercator.x(mapParameters.getZoom(), trajectory.get(i).pos().longitude());
            double y = WebMercator.y(mapParameters.getZoom(), trajectory.get(i).pos().latitude());

            line.setStartX(previousX);
            line.setStartY(previousY);
            line.setEndX(x);
            line.setEndY(y);

            previousX = x;
            previousY = y;

            if (i == 0) continue;

            double currentAltitude = trajectory.get(i).altitude();
            double previousAltitude = trajectory.get(i - 1).altitude();

            Color c0 = ColorRamp.PLASMA.at(calculateColor(previousAltitude));
            Color c1 = ColorRamp.PLASMA.at(calculateColor(currentAltitude));
            Stop s0 = new Stop(1, c0);
            Stop s1 = new Stop(0, c1);

            line.setStroke((currentAltitude == previousAltitude) ?
                    c1 :
                    new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, s0, s1));

            lineList.add(line);
        }
        trajectoryGroup.getChildren().addAll(lineList);
    }

    /**
     * Calculates a color value based on an aircraft's altitude.
     * The color is determined by a mathematical formula involving the aircraft's altitude.
     *
     * @param altitude the altitude of the aircraft
     * @return a double representing the color value
     */
    private double calculateColor(double altitude) {
        return Math.pow(altitude / MAX_ALTITUDE, POWER_FACTOR);
    }

    /**
     * Creates a label Group for a given ObservableAircraftState.
     * The label contains information about the aircraft and is made visible based on the map's zoom level
     * and whether the aircraft state matches the selected aircraft state.
     *
     * @param aircraftState the ObservableAircraftState to create a label for
     * @return a Group representing the label
     */
    private Group label(ObservableAircraftState aircraftState) {
        Text txt = new Text();
        Rectangle rect = new Rectangle();

        txt.textProperty().bind(aircraftInfos(aircraftState));

        rect.widthProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getWidth() + OFFSET));
        rect.heightProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getHeight() + OFFSET));

        Group labelGroup = new Group(rect, txt);
        labelGroup.getStyleClass().add("label");

        labelGroup.visibleProperty().bind(Bindings.lessThanOrEqual(MAX_VISIBLE_ZOOM, mapParameters.zoomProperty()).
                or(selectedAircraftStateProperty.isEqualTo(aircraftState)));

        return labelGroup;
    }

    /**
     * Formats information about an ObservableAircraftState into a StringExpression.
     * The formatted string includes the aircraft identifier, speed in km/h, and altitude in meters.
     *
     * @param aircraftState the ObservableAircraftState to format information for
     * @return a StringExpression containing the formatted information
     */
    private StringExpression aircraftInfos(ObservableAircraftState aircraftState) {
        return Bindings.format("%s \n%s km/h\u2002%s m",
                getIdentifier(aircraftState),
                giveValueOf(aircraftState.velocityProperty(), Units.Speed.KILOMETER_PER_HOUR),
                giveValueOf(aircraftState.altitudeProperty(), Units.Length.METER));
    }

    /**
     * Chooses an identifier for an ObservableAircraftState.
     * The identifier is chosen based on the availability of a call sign, aircraft data, or ICAO address.
     *
     * @param aircraftState the ObservableAircraftState to choose an identifier for
     * @return a String representing the chosen identifier
     */
    private ObservableValue<String> getIdentifier(ObservableAircraftState aircraftState) {
        ReadOnlyObjectProperty<CallSign> callSign = aircraftState.callSignProperty();
        AircraftData data = aircraftState.getAircraftData();
        IcaoAddress icaoAddress = aircraftState.getIcaoAddress();

        return (data != null) ? new SimpleStringProperty(data.registration().string()) :
                Bindings.when(callSign.isNotNull())
                        .then(Bindings.convert(callSign.map(CallSign::string)))
                        .otherwise(icaoAddress.string());
    }

    /**
     * Returns a formatted string of a given numerical property and unit.
     * If the property's current value is NaN, returns "?".
     *
     * @param numExpression the property to format
     * @param unit          the unit of the property
     * @return a ObservableValue<String> containing the formatted property value
     */
    private ObservableValue<String> giveValueOf(DoubleExpression numExpression, double unit) {
        return numExpression.map(v ->
                Double.isNaN(numExpression.doubleValue()) ? "?" :
                        "%.0f".formatted(Units.convertTo(numExpression.doubleValue(), unit)));
    }

    /**
     * Creates an SVGPath icon for a given ObservableAircraftState.
     * The icon is positioned and styled based on properties of the ObservableAircraftState and the selected aircraft state.
     *
     * @param aircraftState the ObservableAircraftState to create an icon for
     * @return an SVGPath representing the icon
     */
    private SVGPath icon(ObservableAircraftState aircraftState) {
        ObservableValue<AircraftIcon> iconProperty = aircraftState.categoryProperty().map(c -> getIcon(aircraftState));

        SVGPath iconPath = new SVGPath();
        iconPath.getStyleClass().add("aircraft");

        iconPath.contentProperty().bind(iconProperty.map(AircraftIcon::svgPath));

        iconPath.rotateProperty().bind(Bindings.createDoubleBinding(() -> iconProperty.getValue().canRotate() ?
                        Units.convertTo(aircraftState.getTrackOrHeading(), Units.Angle.DEGREE) : 0,
                iconProperty, aircraftState.trackOrHeadingProperty()));

        iconPath.fillProperty().bind(aircraftState.altitudeProperty().map(c ->
                ColorRamp.PLASMA.at(calculateColor(c.doubleValue()))));

        iconPath.setOnMouseClicked(e -> selectedAircraftStateProperty.set(aircraftState));

        return iconPath;
    }

    /**
     * Retrieves the appropriate AircraftIcon for a given ObservableAircraftState.
     * The icon is determined based on aircraft data, such as type designator, description,
     * wake turbulence category, and the category of the aircraft state.
     *
     * @param state the ObservableAircraftState to retrieve an icon for
     * @return an AircraftIcon corresponding to the given aircraft state
     */
    private AircraftIcon getIcon(ObservableAircraftState state) {
        AircraftData data = state.getAircraftData();

        AircraftTypeDesignator typeDesignator = (data != null) ?
                data.typeDesignator() : new AircraftTypeDesignator("");
        AircraftDescription aircraftDescription = (data != null) ?
                data.description() : new AircraftDescription("");
        WakeTurbulenceCategory wakeTurbulenceCategory = (data != null) ?
                data.wakeTurbulenceCategory() : WakeTurbulenceCategory.of("");

        return AircraftIcon.iconFor(typeDesignator, aircraftDescription,
                state.getCategory(), wakeTurbulenceCategory);
    }
}