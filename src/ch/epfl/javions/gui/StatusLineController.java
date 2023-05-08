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
        creatPane();
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

    private void creatPane(){
        borderPane = new BorderPane();
        borderPane.getStylesheets().add("/status.css");

        Text textL = configureText("Aéronefs visibles : " + aircraftCountProperty.get());
        Text textR = configureText("Messages reçus : " + messageCountProperty.get());

        borderPane.setLeft(textL);
        borderPane.setRight(textR);
    }

    private Text configureText(String s){
        Text text = new Text();
        text.textProperty().bind(Bindings.createStringBinding(() -> s));
        return text;
    }
}
