package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.function.Consumer;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

public final class AircraftTableController
{
    private final ObservableSet<ObservableAircraftState> statesAccumulatorList;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private TableView<ObservableAircraftState> tableView;


    public AircraftTableController(ObservableSet<ObservableAircraftState> statesAccumulatorList,
                                   ObjectProperty<ObservableAircraftState> selectedAircraftState)
    {
        this.statesAccumulatorList = statesAccumulatorList;
        this.selectedAircraftState = selectedAircraftState;
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
                        tableView.getItems().add(change.getElementRemoved());
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
        this.tableView = new TableView<>();
        tableView.getColumns().addAll(stringColumns());
        tableView.getColumns().addAll(numericalColumns());

        tableView.getStylesheets().add("/table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
    }

    private List<TableColumn<ObservableAircraftState, String>> stringColumns(){

        TableColumn<ObservableAircraftState, String> icaoAddressColumn = new TableColumn<>("OACI");
        icaoAddressColumn.setPrefWidth(60);
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>("Indicatif");
        callSignColumn.setPrefWidth(70);
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>("Immatriculation");
        registrationColumn.setPrefWidth(90);
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Modele");
        modelColumn.setPrefWidth(230);
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setPrefWidth(50);
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setPrefWidth(70);


        List<TableColumn<ObservableAircraftState, String>> stringColumns =
                List.of(icaoAddressColumn, callSignColumn,
                        registrationColumn, modelColumn,
                        typeColumn, descriptionColumn);

        stringColumns.forEach(column ->
                column.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(CallSign::string)));

        return stringColumns;
    }

    private List<TableColumn<ObservableAircraftState, Double>> numericalColumns(){
        List<TableColumn<ObservableAircraftState, Double>> numericalColumns = List.of(
                new TableColumn<>("Longitude (°)"),
                new TableColumn<>("Latitude (°)"),
                new TableColumn<>("Altitude (m)"),
                new TableColumn<>("Vitesse (km/h)")
        );
        numericalColumns.forEach(column -> {
            column.setPrefWidth(85);
            column.getStyleClass().add("numeric");
            NumberFormat.getInstance().format(column);
        });
        return numericalColumns;
    }
}
