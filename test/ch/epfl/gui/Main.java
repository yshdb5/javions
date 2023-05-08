package ch.epfl.gui;

import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;

public final class Main extends Application {
    private final Thread thread = new Thread();
    public static void main(String[] args) {
        launch(args);
        new Thread(() -> null);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp = new MapParameters(17, 17_389_327, 11_867_430);
        BaseMapController bmc = new BaseMapController(tm, mp);

        URL u = getClass().getResource("/aircraft.zip");
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> asp = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), asp);
        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());

        AircraftTableController atc = new AircraftTableController(asm.states(), asp);
        StatusLineController slc = new StatusLineController();
        BorderPane borderPane = new BorderPane(atc.pane(), slc.pane(), null, null,  null);

        SplitPane splitPane = new SplitPane(stackPane, borderPane);

        primaryStage.setScene(new Scene(splitPane));
        primaryStage.show();
    }
}
