package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.function.Consumer;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

public final class AircraftTableController
{
    private final ObservableSet<ObservableAircraftState> statesAccumulatorList;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private TableView<ObservableAircraftState> pane;


    public AircraftTableController(ObservableSet<ObservableAircraftState> statesAccumulatorList,
                                   ObjectProperty<ObservableAircraftState> selectedAircraftState)
    {
        this.statesAccumulatorList = statesAccumulatorList;
        this.selectedAircraftState = selectedAircraftState;
        tableConfiguration();
    }

    public TableView<ObservableAircraftState> pane()
    {
        return pane;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer)
    {
        pane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY))
            {
                if (selectedAircraftState.get() != null)
                {
                    consumer.accept(selectedAircraftState.get());
                }
            }
        });
    }

    private void tableConfiguration(){
        this.pane = new TableView<>();

        pane.getStylesheets().add("/table.css");
        pane.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        pane.setTableMenuButtonVisible(true);
    }

    private void columnsConfiguration(){
        TableColumn<IcaoAddress, String> icaoAddressColumn = new TableColumn<>("OACI");
        TableColumn<CallSign, String> callSignColumn = new TableColumn<>("Indicatif");
        TableColumn<AircraftData, String> modelColumn = new TableColumn<>("Modele");
        TableColumn<AircraftTypeDesignator, String> typeColumn = new TableColumn<>("Type");
        TableColumn<AircraftDescription, String> descriptionColumn = new TableColumn<>("Description");
        TableColumn<Double, Double> longitudeColumn = new TableColumn<>("Longitude (°)");
        TableColumn<Double, Double> latitudeColumn = new TableColumn<>("Latitude (°)");
        TableColumn<Double, Double> altitudeColumn = new TableColumn<>("Altitude (m)");
        TableColumn<Double, Double> velocityColumn = new TableColumn<>("Vitesse (km/h)");
    }
}
