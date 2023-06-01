package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Final StatusLineController class : manages the status line.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class StatusLineController {
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;
    private BorderPane borderPane;

    /**
     * StatusLineController's default constructor.
     */
    public StatusLineController() {
        aircraftCountProperty = new SimpleIntegerProperty();
        messageCountProperty = new SimpleLongProperty();
        configurePane();
    }

    /**
     * @return the pane of the status line.
     */
    public BorderPane pane() {
        return borderPane;
    }

    /**
     * @return the (modifiable) property containing the number of aircraft currently visible
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    /**
     * @return the (modifiable) property containing the number of messages
     * received since the beginning of the program execution.
     */
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }

    /**
     * Creates the pane for the status line, with two Text objects: one for the number of
     * visible aircraft, and one for the number of received messages. These text objects
     * are bound to the respective properties, so they update automatically.
     */
    private void configurePane() {
        borderPane = new BorderPane();
        borderPane.getStylesheets().add("/status.css");

        borderPane.setLeft(configureText("Aéronefs visibles : ", aircraftCountProperty));
        borderPane.setRight(configureText("Messages reçus : ", messageCountProperty));
    }

    /**
     * Creates a Text object with the given name and value, and binds the text property
     * to the value property, so that the text is updated automatically.
     *
     * @param name  the name of the property
     * @param value the value of the property
     * @return the created Text object
     */
    private Text configureText(String name, ObservableValue<? extends Number> value)
    {
        Text text = new Text();
        text.textProperty().bind(Bindings.createStringBinding(() -> name + value.getValue(), value));
        return text;
    }
}
