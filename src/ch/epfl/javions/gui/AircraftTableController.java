package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.function.Consumer;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

public final class AircraftTableController
{
    private final ObservableSet<ObservableAircraftState> statesAccumulatorList;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final TableView<ObservableAircraftState> tableView;

    public AircraftTableController(ObservableSet<ObservableAircraftState> statesAccumulatorList,
                                   ObjectProperty<ObservableAircraftState> selectedAircraftState)
    {
        this.statesAccumulatorList = statesAccumulatorList;
        this.selectedAircraftState = selectedAircraftState;
        this.tableView = new TableView<>();
        tableConfiguration();
        setListeners();
    }

    public TableView<ObservableAircraftState> pane()
    {
        return tableView;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer)
    {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY))
            {
                if (selectedAircraftState.get() != null)
                {
                    consumer.accept(selectedAircraftState.get());
                }
            }
        });
    }

    private void setListeners()
    {
        statesAccumulatorList.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        ObservableAircraftState aircraftState = change.getElementAdded();
                        tableView.getItems().add(aircraftState);
                        tableView.sort();
                    }
                    if (change.wasRemoved())
                        tableView.getItems().remove(change.getElementRemoved());
                });

        selectedAircraftState.addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                tableView.getSelectionModel().select(newValue);
                if(!newValue.equals(selectedAircraftState.get()))
                {
                    tableView.scrollTo(newValue);
                }
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                selectedAircraftState.set(newValue);
            }
        });
    }

    private void tableConfiguration(){
        tableView.getColumns().addAll(stringColumns());
        tableView.getColumns().addAll(numericalColumns());

        tableView.getStylesheets().add("/table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
    }

    private List<TableColumn<ObservableAircraftState, String>> stringColumns(){
        TableColumn<ObservableAircraftState, String> icaoAddressColumn = new TableColumn<>("OACI");
        icaoAddressColumn.setPrefWidth(60);

        icaoAddressColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().getIcaoAddress()).map(IcaoAddress::string));

        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>("Indicatif");
        callSignColumn.setPrefWidth(70);
        callSignColumn.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(CallSign::string));

        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>("Immatriculation");
        registrationColumn.setPrefWidth(90);
        registrationColumn.setCellValueFactory(f ->
                f.getValue().getAircraftData() == null ? new ReadOnlyObjectWrapper<>("") :
                new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().registration())
                        .map(AircraftRegistration::string));

        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Modele");
        modelColumn.setPrefWidth(230);
        modelColumn.setCellValueFactory(f ->
                f.getValue().getAircraftData() == null ? new ReadOnlyObjectWrapper<>("") :
                new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().model()));

        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setPrefWidth(50);
        typeColumn.setCellValueFactory(f ->
                f.getValue().getAircraftData() == null ? new ReadOnlyObjectWrapper<>("") :
                new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().typeDesignator())
                        .map(AircraftTypeDesignator::string));

        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setPrefWidth(70);
        descriptionColumn.setCellValueFactory(f ->
                f.getValue().getAircraftData() == null ? new ReadOnlyObjectWrapper<>("") :
                new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().description()).map(AircraftDescription::string));


        return List.of(icaoAddressColumn, callSignColumn, registrationColumn,
                modelColumn, typeColumn, descriptionColumn);
    }

    private List<TableColumn<ObservableAircraftState, String>> numericalColumns(){
        NumberFormat numberFormat1 = NumberFormat.getInstance();
        numberFormat1.setMaximumFractionDigits(4);
        numberFormat1.setMinimumFractionDigits(0);

        NumberFormat numberFormat2 = NumberFormat.getInstance();
        numberFormat2.setMaximumFractionDigits(0);
        numberFormat2.setMinimumFractionDigits(0);

        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>("Longitude (°)");
        longitudeColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(numberFormat1.format(
                        Units.convertTo(f.getValue().getPosition().longitude(), Units.Angle.DEGREE))));

        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>("Latitude (°)");
        latitudeColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(numberFormat1.format(
                        Units.convertTo(f.getValue().getPosition().latitude(), Units.Angle.DEGREE))));

        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude (m)");
        altitudeColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(numberFormat2.format(f.getValue().getAltitude())));

        TableColumn<ObservableAircraftState, String> velocityColumn = new TableColumn<>("Vitesse (km/h)");
        velocityColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(numberFormat2.format(
                        Units.convertTo(f.getValue().getVelocity(), Units.Speed.KILOMETER_PER_HOUR))));

        List<TableColumn<ObservableAircraftState, String>> numericalColumns = List.of(
                longitudeColumn, latitudeColumn, altitudeColumn, velocityColumn);

        numericalColumns.forEach(column -> {
            column.setPrefWidth(85);
            column.getStyleClass().add("numeric");
            column.setComparator((o1, o2) -> {
                if (o1.isEmpty() || o2.isEmpty())
                    return o1.compareTo(o2);
                else
                    return Double.compare(Double.parseDouble(o1), Double.parseDouble(o2));
                // a corriger
            });
        });
        return numericalColumns;
    }
}
