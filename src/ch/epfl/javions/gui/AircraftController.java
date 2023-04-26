package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import javax.print.DocFlavor;

public final class AircraftController {
    private MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList;
    private ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty;
    private Pane pane;
    private Group aircraftGroup;
    private Group trajectoryGroup;
    private Group intermediaryGroup;
    private Group labelGroup;
    private SVGPath aircraftPath;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList,
                              ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty) {
        this.mapParameters = mapParameters;
        this.unmodifiableStatesAccumulatorList = unmodifiableStatesAccumulatorList;
        this.selectedAircraftStateProperty = selectedAircraftStateProperty;
        this.pane = new Pane();

        this.pane = new Pane();

        this.aircraftGroup = new Group();
        this.trajectoryGroup = new Group();
        this.intermediaryGroup = new Group();
        this.labelGroup = new Group();
        this.aircraftPath = new SVGPath();

        Rectangle labelRect = new Rectangle();
        Text labelText = new Text();

        pane.getChildren().add(aircraftGroup);
        aircraftGroup.getChildren().add(trajectoryGroup);
        intermediaryGroup.getChildren().addAll(labelGroup, aircraftPath);
        labelGroup.getChildren().addAll(labelRect, labelText);

        pane.setPickOnBounds(false);
        pane.getStylesheets().add("/resources/aircraft.css");
        aircraftGroup.setId(selectedAircraftStateProperty.get().getIcaoAddress().string());
        aircraftGroup.getStyleClass().add(trajectoryGroup.getId());
        intermediaryGroup.getStyleClass().addAll(labelGroup.getId(), aircraftPath.getId());

        unmodifiableStatesAccumulatorList.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
            if (change.wasAdded()) pane.getChildren().add(new Group());
            if (change.wasRemoved()) pane.getChildren().remove();
        });
    }

    public Pane pane() {return pane;}


}
