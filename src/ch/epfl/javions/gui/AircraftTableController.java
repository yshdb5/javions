package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
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
import java.util.function.Function;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

public final class AircraftTableController
{
    public static final int NUM_COLUMN_WIDTH = 85;
    public static final int ICAO_COLUMN_WIDTH = 60;
    public static final int DESCRIPTION_COLUMN_WIDTH = 70;
    public static final int CALLSIGN_COLUMN_WIDTH = 70;
    public static final int REGISTRATION_COLUMN_WIDTH = 90;
    public static final int MODEL_COLUMN_WIDTH = 230;
    public static final int TYPE_COLUMN_WIDTH = 50;
    public static final int MAX_FRACTION_DIGITS = 4;
    public static final int MIN_FRACTION_DIGITS = 0;
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
        tableView.getStylesheets().add("/table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        tableView.getColumns().addAll(columns());
    }

    private List<TableColumn<ObservableAircraftState, String>> columns(){
        NumberFormat numberFormat1 = configureFormat(MAX_FRACTION_DIGITS);
        NumberFormat numberFormat2 = configureFormat(MIN_FRACTION_DIGITS);


        return List.of(
                createTextColumn("OACI", ICAO_COLUMN_WIDTH,
                        f -> f.getIcaoAddress().string()),
                createTextColumn("Indicatif", CALLSIGN_COLUMN_WIDTH,
                        f -> f.getCallSign() == null ? "" : f.getCallSign().string()),
                createTextColumn("Immatriculation", REGISTRATION_COLUMN_WIDTH,
                        f -> f.getAircraftData() == null ? "" : f.getAircraftData().registration().string()),
                createTextColumn("Modele", MODEL_COLUMN_WIDTH,
                        f -> f.getAircraftData() == null ? "" : f.getAircraftData().model()),
                createTextColumn("Type", TYPE_COLUMN_WIDTH,
                        f -> f.getAircraftData() == null ? "" : f.getAircraftData().typeDesignator().string()),
                createTextColumn("Description", DESCRIPTION_COLUMN_WIDTH,
                        f -> f.getAircraftData() == null ? "" : f.getAircraftData().description().string()),

                createNumColumn("Longitude (°)", f -> numberFormat1.format(
                        Units.convertTo(f.getPosition().longitude(), Units.Angle.DEGREE))),
                createNumColumn("Latitude (°)", f -> numberFormat1.format(
                        Units.convertTo(f.getPosition().latitude(), Units.Angle.DEGREE))),
                createNumColumn("Altitude (m)", f -> numberFormat2.format(f.getAltitude())),
                createNumColumn("Vitesse (km/h)", f -> {
                    double velocity = f.getVelocity();
                    return Double.isNaN(velocity) ? "" : numberFormat2.format(velocity);
                }))
    ;}

    private TableColumn<ObservableAircraftState, String> createColumn(
            String name, Function<ObservableAircraftState, String> valueExtractor) {

        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(name);
        column.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valueExtractor.apply(f.getValue())));

        return column;
    }

    private TableColumn<ObservableAircraftState, String> createNumColumn(
            String name, Function<ObservableAircraftState, String> valueFactory) {

        TableColumn<ObservableAircraftState, String> column = createColumn(name, valueFactory);
        column.setPrefWidth(NUM_COLUMN_WIDTH);
        column.getStyleClass().add("numeric");
        NumberFormat format = NumberFormat.getInstance();
        column.setComparator((o1, o2) -> {
            if (o1.isEmpty() || o2.isEmpty())
                return o1.compareTo(o2);
            else {
                try {
                    return Double.compare(format.parse(o1).doubleValue(), format.parse(o2).doubleValue());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return column;
    }

    private TableColumn<ObservableAircraftState, String> createTextColumn(
            String name, int width, Function<ObservableAircraftState, String> valueFactory) {

        TableColumn<ObservableAircraftState, String> column = createColumn(name, valueFactory);
        column.setPrefWidth(width);

        return column;
    }

    private NumberFormat configureFormat(int maxFractionDigits){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(maxFractionDigits);
        numberFormat.setMinimumFractionDigits(MIN_FRACTION_DIGITS);
        return numberFormat;
    }


}
