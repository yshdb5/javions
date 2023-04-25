package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Group;
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

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> unmodifiableStatesAccumulatorList,
                              ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty) {
        this.mapParameters = mapParameters;
        this.unmodifiableStatesAccumulatorList = unmodifiableStatesAccumulatorList;
        this.selectedAircraftStateProperty = selectedAircraftStateProperty;
        this.pane = new Pane();

        this.pane = new Pane();

        Group aircraftGroup = new Group();
        Group trajectoryGroup = new Group();
        Group group3 = new Group();
        Group labelGroup = new Group();
        Rectangle labelRect = new Rectangle();
        Text labelText = new Text();
        SVGPath aircraftPath = new SVGPath();


        pane.getChildren().add(aircraftGroup);
        aircraftGroup.getChildren().add(trajectoryGroup);
        group3.getChildren().addAll(labelGroup, aircraftPath);
        labelGroup.getChildren().addAll(labelRect, labelText);


        pane.setPickOnBounds(false);
        pane.getStylesheets().add("/resources/aircraft.css");
        aircraftGroup.setId(selectedAircraftStateProperty.get().getIcaoAddress().string());
        aircraftGroup.getStyleClass().add(trajectoryGroup.getId());
        group3.getStyleClass().addAll(labelGroup.getId(), aircraftPath.getId());
    }

    public Pane pane() {return pane;}


}
