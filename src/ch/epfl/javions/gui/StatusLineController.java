package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public final class StatusLineController
{
    private BorderPane borderPane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;
    public StatusLineController(){
        aircraftCountProperty = new SimpleIntegerProperty();
        messageCountProperty = new SimpleLongProperty();
        createPane();
    }
    public BorderPane pane(){
        return borderPane;
    }

    public IntegerProperty aircraftCountProperty(){
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty(){
        return messageCountProperty;
    }

    private void createPane(){
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
