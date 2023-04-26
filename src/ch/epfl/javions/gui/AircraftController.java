package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public final class AircraftController {
    private MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList;
    private ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty;
    private Pane pane;

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
                        String id = change.getElementAdded().getIcaoAddress().string();
                        Group aircraftGroup = new Group();
                        aircraftGroup.setId(id);
                        aircraftGroup.viewOrderProperty().bind(change.getElementAdded().altitudeProperty().negate());
                        trajectory(aircraftGroup);
                        Group intermediaryGroup = new Group();
                        label(intermediaryGroup);
                        icon(intermediaryGroup);
                        aircraftGroup.getChildren().add(intermediaryGroup);
                        pane.getChildren().add(aircraftGroup);
                    }
                    if (change.wasRemoved())
                        pane.getChildren().removeIf(e ->
                                e.getId().equals(change.getElementRemoved().getIcaoAddress().string()));
                });
    }
    private void trajectory(Group parent){
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");
        parent.getChildren().add(trajectoryGroup);
    }
    private void label(Group parent){
        Group labelGroup = new Group();
        pane.getStyleClass().add("label");
        Text txt = new Text();
        Rectangle rect = new Rectangle();
        rect.widthProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        parent.getChildren().add(labelGroup);
    }
    private void icon(Group parent){
        SVGPath iconPath = new SVGPath();
        iconPath.getStyleClass().add("aircraft");
        parent.getChildren().add(iconPath);
    }
}
