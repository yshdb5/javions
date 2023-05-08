package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public final class StatusLineController
{
    private BorderPane borderPane;
    public StatusLineController(){
        creatPane();
    }
    private BorderPane pane(){
        return borderPane;
    }

    public IntegerProperty aircraftCountProperty(){
        return null;
    }

    public LongProperty messageCountProperty(){
        return null;
    }

    private void creatPane(){
        borderPane = new BorderPane();
        borderPane.getStylesheets().add("/status.css");

        Text textL = configureText("Aéronefs visibles : " + aircraftCountProperty().get());
        Text textR = configureText("Messages reçus : " + messageCountProperty().get());

        borderPane.setLeft(textL);
        borderPane.setRight(textR);
    }

    private Text configureText(String s){
        Text text = new Text();
        text.textProperty().bind(Bindings.createStringBinding(
                () -> s));
        return text;
    }
}
