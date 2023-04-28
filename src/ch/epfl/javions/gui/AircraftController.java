package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class AircraftController {
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty;
    private final Pane pane;

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

    public Pane pane() {return pane;}

    private void setListeners()
    {
        unmodifiableStatesAccumulatorList.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        ObservableAircraftState aircraftState = change.getElementAdded();

                        annotatedAircraft(aircraftState);
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
        Group iconLabelGroup = new Group(icon(aircraftState), label(aircraftState));

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

        trajectoryGroup.visibleProperty().addListener((object, oldVisible, newVisible) ->
        {
            if (newVisible)
            {
                redrawTrajectory(aircraftState.getTrajectory(), trajectoryGroup);
                mapParameters.zoomProperty().addListener(z -> redrawTrajectory(aircraftState.getTrajectory(), trajectoryGroup));
            }
        });
        return trajectoryGroup;
    }

    private void redrawTrajectory(List<ObservableAircraftState.AirbornePos> trajectory, Group trajectoryGroup){
        if (trajectory.size() < 2) return;
        List<Line> lineList = new ArrayList<>();
        double previousX = 0;
        double previousY = 0;

        for (int i = 0; i < trajectory.size(); ++i) {
            Line line = new Line();

            double x = WebMercator.x(mapParameters.getZoom(), trajectory.get(i).pos().longitude());
            double y = WebMercator.y(mapParameters.getZoom(), trajectory.get(i).pos().latitude());

            if (i == 0) continue;

            line.setStartX(previousX);
            line.setStartY(previousY);
            line.setEndX(x);
            line.setEndY(y);

            previousX = x;
            previousY =y;

            lineList.add(line);
        }

        trajectoryGroup.getChildren().addAll(lineList);
    };
    private Group label(ObservableAircraftState aircraftState){
        Text txt = new Text();
        Rectangle rect = new Rectangle();

        txt.textProperty().bind(
                Bindings.format("%s \n %5f km/h %5f m",
                        (aircraftState.callSignProperty().get() != null)? aircraftState.callSignProperty().get().string() : "",
                        aircraftState.velocityProperty(),
                        aircraftState.altitudeProperty()));
        rect.widthProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rect.heightProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getHeight() + 4));

        Group labelGroup = new Group(txt, rect);
        labelGroup.getStyleClass().add("label");

        labelGroup.visibleProperty().bind(Bindings.lessThanOrEqual(11, mapParameters.zoomProperty()).
                or(selectedAircraftStateProperty.isEqualTo(aircraftState)));

        return labelGroup;
    }
    private SVGPath icon(ObservableAircraftState aircraftState){
        AircraftData data = aircraftState.getAircraftData();
        AircraftTypeDesignator typeDesignator = (data != null)?
                data.typeDesignator() : new AircraftTypeDesignator("");
        AircraftDescription aircraftDescription = (data != null)?
                data.description() : new AircraftDescription("");
        WakeTurbulenceCategory wakeTurbulenceCategory = (data != null)?
                data.wakeTurbulenceCategory() : WakeTurbulenceCategory.of("");

        AircraftIcon icon = AircraftIcon.iconFor(typeDesignator, aircraftDescription,
                aircraftState.getCategory(), wakeTurbulenceCategory);

        ObjectProperty<AircraftIcon> iconProperty =  new SimpleObjectProperty<>(icon);

        SVGPath iconPath = new SVGPath();
        iconPath.getStyleClass().add("aircraft");

        iconPath.contentProperty().bind(iconProperty.map(AircraftIcon::svgPath));
        iconPath.rotateProperty().bind(Bindings.createDoubleBinding(()-> iconProperty.get().canRotate()?
                Units.convertTo(aircraftState.getTrackOrHeading(), Units.Angle.DEGREE) : 0, iconProperty, aircraftState.trackOrHeadingProperty()));
        iconPath.fillProperty().bind(aircraftState.altitudeProperty().map(c -> ColorRamp.PLASMA.at(1/c.doubleValue())));

        iconPath.setOnMouseClicked(e -> selectedAircraftStateProperty.set(aircraftState));

        return iconPath;
    }
}
