package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
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

/**
 * Final class AircraftTableController : manage the aircraft table.
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class AircraftTableController {
    private static final int NUM_COLUMN_WIDTH = 85;
    private static final int ICAO_COLUMN_WIDTH = 60;
    private static final int DESCRIPTION_COLUMN_WIDTH = 70;
    private static final int CALLSIGN_COLUMN_WIDTH = 70;
    private static final int REGISTRATION_COLUMN_WIDTH = 90;
    private static final int MODEL_COLUMN_WIDTH = 230;
    private static final int TYPE_COLUMN_WIDTH = 50;
    private static final int MAX_FRACTION_DIGITS = 4;
    private static final int MIN_FRACTION_DIGITS = 0;
    private final ObservableSet<ObservableAircraftState> statesAccumulatorList;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final TableView<ObservableAircraftState> tableView;

    /**
     * AircraftTableController constructor.
     *
     * @param statesAccumulatorList the set (observable but not modifiable) of aircraft states that should appear on the view.
     * @param selectedAircraftState the property (observable) of the selected aircraft state.
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> statesAccumulatorList,
                                   ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        this.statesAccumulatorList = statesAccumulatorList;
        this.selectedAircraftState = selectedAircraftState;
        this.tableView = new TableView<>();
        tableConfiguration();
        setListeners();
    }

    /**
     * Creates a column with a text value.
     * This method creates and returns the TableView instance containing
     * ObservableAircraftStates that represents the aircraft table in the pane.
     * @return TableView<ObservableAircraftState> instance.
     *
     */
    public TableView<ObservableAircraftState> pane() {
        return tableView;
    }

    /**
     * Calls the accept method of the consumer when a double click is done on the table and
     * an aircraft is currently selected, passing it as argument the state of this aircraft.
     *
     * @param consumer the consumer to be called when the user double-clicks on a row.
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                if (selectedAircraftState.get() != null) {
                    consumer.accept(selectedAircraftState.get());
                }
            }
        });
    }

    /**
     * Registers listeners on the observable set of aircraft states and the selected
     * aircraft state property. These listeners respond to changes in the aircraft
     * states set and the selected aircraft state.
     */
    private void setListeners() {
        statesAccumulatorList.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        tableView.getItems().add(change.getElementAdded());
                        tableView.sort();
                    }
                    if (change.wasRemoved())
                        tableView.getItems().remove(change.getElementRemoved());
                });

        selectedAircraftState.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(tableView.getSelectionModel().getSelectedItem())) {
                tableView.scrollTo(newValue);
            }

            tableView.getSelectionModel().select(newValue);
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedAircraftState.set(newValue);
            }
        });
    }

    /**
     * Configures the TableView instance by setting its style, column resize policy,
     * visibility of table menu button, and adding table columns.
     */

    private void tableConfiguration() {
        tableView.getStylesheets().add("/table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        tableView.getColumns().addAll(columns());
    }

    /**
     * Creates and returns a list of table columns, each of which represents
     * a property of the ObservableAircraftState object.
     *
     * @return List of TableColumn<ObservableAircraftState, String> instances.
     */

    private List<TableColumn<ObservableAircraftState, String>> columns() {
        return List.of(
                createTextColumn("OACI", ICAO_COLUMN_WIDTH,
                        f -> (new ReadOnlyStringWrapper(f.getIcaoAddress().string()))),
                createTextColumn("Indicatif", CALLSIGN_COLUMN_WIDTH,
                        f -> (f.callSignProperty().map(CallSign::string))),
                createTextColumn("Immatriculation", REGISTRATION_COLUMN_WIDTH,
                        f -> (new ReadOnlyStringWrapper(f.getAircraftData() == null ? "" :
                                f.getAircraftData().registration().string()))),
                createTextColumn("Modele", MODEL_COLUMN_WIDTH,
                        f -> (new ReadOnlyStringWrapper(f.getAircraftData() == null ? "" :
                                f.getAircraftData().model()))),
                createTextColumn("Type", TYPE_COLUMN_WIDTH,
                        f -> (new ReadOnlyStringWrapper(f.getAircraftData() == null ? "" :
                                f.getAircraftData().typeDesignator().string()))),
                createTextColumn("Description", DESCRIPTION_COLUMN_WIDTH,
                        f -> (new ReadOnlyStringWrapper(f.getAircraftData() == null ? "" :
                                f.getAircraftData().description().string()))),

                createNumColumn("Longitude (°)",
                        f -> Bindings.createDoubleBinding(() -> f.getPosition().longitude(), f.positionProperty()),
                        MAX_FRACTION_DIGITS, Units.Angle.DEGREE),
                createNumColumn("Latitude (°)",
                        f -> Bindings.createDoubleBinding(() -> f.getPosition().latitude(), f.positionProperty()),
                        MAX_FRACTION_DIGITS, Units.Angle.DEGREE),
                createNumColumn("Altitude (m)",
                        ObservableAircraftState::altitudeProperty, MIN_FRACTION_DIGITS, Units.Length.METER),
                createNumColumn("Vitesse (km/h)",
                        ObservableAircraftState::velocityProperty, MIN_FRACTION_DIGITS, Units.Speed.KILOMETER_PER_HOUR));
    }


    /**
     * Creates and returns a TableColumn instance for a numeric property
     * of the ObservableAircraftState object.
     *
     * @param name Name of the column.
     * @param valueFactory Function to extract the numeric value from the ObservableAircraftState.
     * @param fractionDigits Number of digits after the decimal point to display.
     * @param unit Unit of the numeric value.
     * @return TableColumn<ObservableAircraftState, String> instance.
     */

    private TableColumn<ObservableAircraftState, String> createNumColumn(
            String name, Function<ObservableAircraftState, DoubleExpression> valueFactory, int fractionDigits,
            double unit) {

        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(name);

        NumberFormat numFormat = configureFormat(fractionDigits);

        column.setCellValueFactory(f ->
                valueFactory.apply(f.getValue()).map(v -> Double.isNaN(v.doubleValue()) ? "" :
                        numFormat.format(Units.convertTo(valueFactory.apply(f.getValue()).doubleValue(), unit))));

        column.setPrefWidth(NUM_COLUMN_WIDTH);
        column.getStyleClass().add("numeric");
        column.setComparator((o1, o2) -> {
            if (o1.isEmpty() || o2.isEmpty())
                return o1.compareTo(o2);
            else {
                try {
                    return Double.compare(numFormat.parse(o1).doubleValue(), numFormat.parse(o2).doubleValue());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return column;
    }

    /**
     * Creates and returns a TableColumn instance for a textual property
     * of the ObservableAircraftState object.
     *
     * @param name Name of the column.
     * @param width Preferred width of the column.
     * @param valueFactory Function to extract the text value from the ObservableAircraftState.
     * @return TableColumn<ObservableAircraftState, String> instance.
     */

    private TableColumn<ObservableAircraftState, String> createTextColumn(
            String name, int width, Function<ObservableAircraftState, ObservableValue<String>> valueFactory) {

        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(name);
        column.setCellValueFactory(f -> valueFactory.apply(f.getValue()));
        column.setPrefWidth(width);

        return column;
    }

    /**
     * Configures and returns a NumberFormat instance that formats numbers
     * with a specified maximum number of fraction digits.
     *
     * @param maxFractionDigits Maximum number of fraction digits.
     * @return NumberFormat instance.
     */

    private NumberFormat configureFormat(int maxFractionDigits) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(maxFractionDigits);
        numberFormat.setMinimumFractionDigits(MIN_FRACTION_DIGITS);
        return numberFormat;
    }
}
