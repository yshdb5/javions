package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Final StatusLineController class : manages the status line.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class StatusLineController {
    private BorderPane borderPane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    /**
     * StatusLineController's default constructor.
     */
    public StatusLineController() {
        aircraftCountProperty = new SimpleIntegerProperty();
        messageCountProperty = new SimpleLongProperty();
        createPane();
    }

    /**
     * @return the pane of the status line.
     */
    public BorderPane pane() {
        return borderPane;
    }

    /**
     * @return the ( modifiable ) property containing the number of aircraft currently visible
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
    private void createPane() {
        borderPane = new BorderPane();
        borderPane.getStylesheets().add("/status.css");

        Text textL = new Text();
        textL.textProperty().bind(Bindings.createStringBinding(() ->
                "Aéronefs visibles : " + aircraftCountProperty.get(), aircraftCountProperty));
        Text textR = new Text();
        textR.textProperty().bind(Bindings.createStringBinding(() ->
                "Messages reçus : " + messageCountProperty.get(), messageCountProperty));

        borderPane.setLeft(textL);
        borderPane.setRight(textR);
    }
}
