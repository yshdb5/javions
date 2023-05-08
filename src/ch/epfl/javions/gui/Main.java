package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Main extends Application {
    public static final int MIN_WIDTH = 800;
    public static final int MIN_HEIGHT = 600;
    public static final int INITIAL_ZOOM = 8;
    public static final int X = 33_530;
    public static final int Y = 23_070;
    private final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final long startTime = System.nanoTime();
    public static void main(String[] args) {
        launch(args);
    }

    //TODO: EN GENERAL
    //TODO: comprendre pourquoi le programme est lent
    // (surement en rapport avec la vitesse de lecture des messages dans l'animation ou le delai dans readAllmessages)
    //TODO: Regarder que toutes les fonctionnalités demandées sont présentent

    @Override
    public void start(Stage primaryStage) throws Exception {
        Path tileCache = Path.of("tile-cache");
        TileManager tileManager = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mapParameters = new MapParameters(INITIAL_ZOOM, X, Y);
        BaseMapController baseMap = new BaseMapController(tileManager, mapParameters);

        URL url = getClass().getResource("/aircraft.zip");
        assert url != null;
        Path path = Path.of(url.toURI());

        AircraftDatabase database = new AircraftDatabase(path.toString());
        AircraftStateManager stateManager = new AircraftStateManager(database);
        ObservableSet<ObservableAircraftState> observableAircraftSet = stateManager.states();
        ObjectProperty<ObservableAircraftState> aircraftStateProperty = new SimpleObjectProperty<>();

        AircraftController aircraftController =
                new AircraftController(mapParameters, observableAircraftSet, aircraftStateProperty);
        AircraftTableController tableController =
                new AircraftTableController(observableAircraftSet, aircraftStateProperty);

        StatusLineController lineController = new StatusLineController();
        //TODO: comprendre pourquoi ca marche pas,
        // les valeurs de aircraftCountProperty et messageCountProperty ne changent pas
        lineController.aircraftCountProperty().bind(Bindings.size(observableAircraftSet));
        LongProperty messageCountProperty = lineController.messageCountProperty();

        StackPane stackPane = new StackPane(baseMap.pane(), aircraftController.pane());
        BorderPane borderPane = new BorderPane(
                tableController.pane(), lineController.pane(), null, null,  null);

        SplitPane splitPane = new SplitPane(stackPane, borderPane);
        splitPane.orientationProperty().set(Orientation.VERTICAL);

        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.show();

        new Thread(() -> {
            List<String> args = getParameters().getRaw();
            if(args.isEmpty())
            {
                try {
                    AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                    while (true) {
                        messageQueue.add(demodulator.nextMessage());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                readAllMessages(args.get(0));
            }
        }).start();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; i++) {
                        RawMessage rawMessage = messageQueue.poll();
                        if (rawMessage == null) return;
                        Message message = MessageParser.parse(rawMessage);
                        if (message != null) {
                            stateManager.updateWithMessage(message);
                            messageCountProperty.set(messageCountProperty.get() + 1);
                        }
                        stateManager.purge();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }

    private void readAllMessages(String fileName){
        String f = getClass().getResource(fileName).getFile();
        f = URLDecoder.decode(f, UTF_8);

        try (DataInputStream stream = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f)))){

            byte[] bytes = new byte[RawMessage.LENGTH];
            while (stream.available() > 0) {
                long timeStampNs = stream.readLong();
                int bytesRead = stream.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                //TODO: verifier comment bien faire ca
                while (System.nanoTime() < timeStampNs + startTime) Thread.sleep(1);
                messageQueue.add(new RawMessage(timeStampNs, message));
            }
        }catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
