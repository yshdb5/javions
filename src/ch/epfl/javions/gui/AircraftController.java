package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

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
        pane.getStylesheets().add("/resources/aircraft.css");

        setListeners();
    }

    public Pane pane() {return pane;}

    private void setListeners()
    {
        unmodifiableStatesAccumulatorList.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        ObservableAircraftState aircraftState = change.getElementAdded();

                        Group annotatedAircraftGroup = annotatedAircraft(aircraftState);
                        Group iconLabelGroup = iconLabel(annotatedAircraftGroup, aircraftState);

                        trajectory(annotatedAircraftGroup, aircraftState);
                        label(iconLabelGroup, aircraftState);
                        icon(iconLabelGroup, aircraftState);
                    }
                    if (change.wasRemoved())
                        pane.getChildren().removeIf(e ->
                                e.getId().equals(change.getElementRemoved().getIcaoAddress().string()));
                });
    }
    private Group annotatedAircraft(ObservableAircraftState aircraftState)
    {
        Group annotatedAircraftGroup = new Group();

        String id = aircraftState.getIcaoAddress().string();
        annotatedAircraftGroup.setId(id);

        annotatedAircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        pane.getChildren().add(annotatedAircraftGroup);
        return annotatedAircraftGroup;
    }

    private Group iconLabel(Group parent, ObservableAircraftState aircraftState)
    {
        Group iconLabelGroup = new Group();

        DoubleProperty xProperty = new SimpleDoubleProperty(
                WebMercator.x(mapParameters.getZoom(), aircraftState.getPosition().longitude()));
        DoubleProperty yProperty = new SimpleDoubleProperty(
                WebMercator.y(mapParameters.getZoom(), aircraftState.getPosition().latitude()));

        iconLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                xProperty.get() - mapParameters.getMinX()));
        iconLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                yProperty.get() - mapParameters.getMinY()));

        parent.getChildren().add(iconLabelGroup);
        return iconLabelGroup;
    }
    private void trajectory(Group parent, ObservableAircraftState aircraftState){
        Group trajectoryGroup = new Group();

        trajectoryGroup.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                aircraftState.equals(selectedAircraftStateProperty.get())));

        aircraftState.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>)
                change -> {});
        //trajectoryGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
        //        WebMercator.x(mapParameters.getZoom(), aircraftState.getTrajectory()));

        trajectoryGroup.getStyleClass().add("trajectory");
        parent.getChildren().add(trajectoryGroup);
    }
    private void label(Group parent, ObservableAircraftState aircraftState){
        Group labelGroup = new Group();
        Text txt = new Text();
        Rectangle rect = new Rectangle();

        txt.textProperty().bind(
                Bindings.format("% s \n %f km/h %f m",
                        aircraftState.callSignProperty(),
                        aircraftState.velocityProperty(),
                        aircraftState.altitudeProperty()));
        rect.widthProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rect.heightProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getHeight() + 4));


        labelGroup.visibleProperty().bind(Bindings.lessThanOrEqual(11, mapParameters.zoomProperty()));

        labelGroup.getStyleClass().add("label");
        labelGroup.getChildren().addAll(txt, rect);
        parent.getChildren().add(labelGroup);
    }
    private void icon(Group parent, ObservableAircraftState aircraftState){
        SVGPath iconPath = new SVGPath();

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

        iconPath.contentProperty().bind(Bindings.createStringBinding(() -> iconProperty.get().svgPath()));
        iconPath.rotateProperty().bind(iconProperty.map(e -> e.canRotate()?
                aircraftState.getTrackOrHeading() : 0));
        iconPath.fillProperty().bind(aircraftState.altitudeProperty().map(c -> ColorRamp.PLASMA.at(1/c.doubleValue())));

        iconPath.setOnMouseClicked(e -> selectedAircraftStateProperty.set(aircraftState));

        iconPath.getStyleClass().add("aircraft");
        parent.getChildren().add(iconPath);
    }
}
