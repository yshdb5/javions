package ch.epfl.javions.gui;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import java.util.List;

/**
 * Final  AircraftController class : manages the view of the aircraft.
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class AircraftController {
    private static final int MAX_ALTITUDE = 12000;
    private static final double POWER_FACTOR = 1d / 3d;
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty;
    private final Pane pane;

    /**
     * AircraftController's constructor.
     * @param mapParameters the parameters of the portion of the map visible on the screen
     * @param unmodifiableStatesAccumulatorList the set (observable but not modifiable) of aircraft states that must appear on the view
     * @param selectedAircraftStateProperty a JavaFX property containing the state of the selected aircraft,
     *                                      whose content can be null when no aircraft is selected.
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

    public Pane pane() {return pane;}

    private void setListeners()
    {
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
    private void annotatedAircraft(ObservableAircraftState aircraftState)
    {
        Group annotatedAircraftGroup = new Group(trajectory(aircraftState), iconLabel(aircraftState));

        String id = aircraftState.getIcaoAddress().string();
        annotatedAircraftGroup.setId(id);

        annotatedAircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        pane.getChildren().add(annotatedAircraftGroup);
    }

    private Group iconLabel(ObservableAircraftState aircraftState)
    {
        Group iconLabelGroup = new Group(label(aircraftState), icon(aircraftState));

        iconLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.x(mapParameters.getZoom(), aircraftState.getPosition().longitude()) - mapParameters.getMinX(),
                aircraftState.positionProperty(), mapParameters.zoomProperty(), mapParameters.minXProperty()));

        iconLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.y(mapParameters.getZoom(), aircraftState.getPosition().latitude()) - mapParameters.getMinY(),
                aircraftState.positionProperty(), mapParameters.zoomProperty(), mapParameters.minYProperty()));

        return iconLabelGroup;
    }
    private Group trajectory(ObservableAircraftState aircraftState){
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");

        trajectoryGroup.visibleProperty().bind(Bindings.equal(aircraftState, selectedAircraftStateProperty));

        trajectoryGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> -mapParameters.getMinX(),
                mapParameters.minXProperty()));
        trajectoryGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> -mapParameters.getMinY(),
                mapParameters.minYProperty()));

        trajectoryGroup.visibleProperty().addListener((object, oldVisible, newVisible) ->
        {
            if (newVisible)
            {
                redrawTrajectory(aircraftState.getTrajectory(), trajectoryGroup);
                mapParameters.zoomProperty().addListener(zoom -> redrawTrajectory(aircraftState.getTrajectory(), trajectoryGroup));
                aircraftState.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>)
                        change -> redrawTrajectory(aircraftState.getTrajectory(), trajectoryGroup));
            }
        });
        return trajectoryGroup;
    }

    private void redrawTrajectory(ObservableList<ObservableAircraftState.AirbornePos> trajectory, Group trajectoryGroup){
        if (trajectory.size() < 2) return;
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

            line.setStroke((currentAltitude == previousAltitude)?
                            c1 :
                    new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, s0, s1));

            lineList.add(line);
        }

        trajectoryGroup.getChildren().addAll(lineList);
    }

    private double calculateColor (double altitude)
    {
        return Math.pow(altitude/ MAX_ALTITUDE, POWER_FACTOR);
    }
    private Group label(ObservableAircraftState aircraftState){
        Text txt = new Text();
        Rectangle rect = new Rectangle();

        //TODO faire une méthode pour rendre le Binding plus clair à chaque fois que l'on fait ? ... : "?";
        txt.textProperty().bind(aircraftInfos(aircraftState));

        rect.widthProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rect.heightProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getHeight() + 4));

        Group labelGroup = new Group(rect, txt);
        labelGroup.getStyleClass().add("label");

        labelGroup.visibleProperty().bind(Bindings.lessThanOrEqual(11, mapParameters.zoomProperty()).
                or(selectedAircraftStateProperty.isEqualTo(aircraftState)));

        return labelGroup;
    }

    private StringExpression aircraftInfos(ObservableAircraftState aircraftState) {
        return Bindings.format("%s \n %s km/h\u2002%s m",
                chooseIdentifier(aircraftState),
                giveValueOf(aircraftState.velocityProperty(), Units.Speed.KILOMETER_PER_HOUR),
                giveValueOf(aircraftState.altitudeProperty(), Units.Length.METER));
    }

    private String chooseIdentifier(ObservableAircraftState aircraftState){
        CallSign callSign = aircraftState.getCallSign();
        AircraftData data = aircraftState.getAircraftData();
        IcaoAddress icaoAddress = aircraftState.getIcaoAddress();
        if (callSign != null) return callSign.string();
        else if (data != null && data.registration() != null) return data.registration().string();
        else return icaoAddress.string();
    }

    private ObservableValue<Integer> giveValueOf(ReadOnlyDoubleProperty value, double unit){
        return value.map(v -> (int) Math.rint(Units.convertTo(v.doubleValue(), unit)));
    }

    private SVGPath icon(ObservableAircraftState aircraftState){
        AircraftIcon icon = getIcon(aircraftState);
        ObjectProperty<AircraftIcon> iconProperty =  new SimpleObjectProperty<>(icon);

        SVGPath iconPath = new SVGPath();
        iconPath.getStyleClass().add("aircraft");

        iconPath.contentProperty().bind(iconProperty.map(AircraftIcon::svgPath));
        iconPath.rotateProperty().bind(Bindings.createDoubleBinding(() -> iconProperty.get().canRotate() ?
                Units.convertTo(aircraftState.getTrackOrHeading(), Units.Angle.DEGREE) : 0,
                iconProperty, aircraftState.trackOrHeadingProperty()));
        iconPath.fillProperty().bind(aircraftState.altitudeProperty().map(c ->
                ColorRamp.PLASMA.at(calculateColor(c.doubleValue()))));

        iconPath.setOnMouseClicked(e -> selectedAircraftStateProperty.set(aircraftState));

        return iconPath;
    }

    private AircraftIcon getIcon(ObservableAircraftState state)
    {
        AircraftData data = state.getAircraftData();
        AircraftTypeDesignator typeDesignator = (data != null)?
                data.typeDesignator() : new AircraftTypeDesignator("");
        AircraftDescription aircraftDescription = (data != null)?
                data.description() : new AircraftDescription("");
        WakeTurbulenceCategory wakeTurbulenceCategory = (data != null)?
                data.wakeTurbulenceCategory() : WakeTurbulenceCategory.of("");

        return AircraftIcon.iconFor(typeDesignator, aircraftDescription,
                state.getCategory(), wakeTurbulenceCategory);
    }
}
