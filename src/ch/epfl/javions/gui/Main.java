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
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Main extends Application {
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final int INITIAL_ZOOM = 8;
    private static final int X = 33_530;
    private static final int Y = 23_070;
    private final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();
    public static void main(String[] args) {
        launch(args);
    }

    // TODO : EN GENERAL
    // TODO :Verifier comment bien gérer la fin du flot de données

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
        tableController.setOnDoubleClick(state -> baseMap.centerOn(state.getPosition()));

        StatusLineController lineController = new StatusLineController();
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

        Thread thread = new Thread(() -> {
            List<String> args = getParameters().getRaw();
            if(args.isEmpty())
            {
                try {
                    AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                    while (System.in.available() > 0) {
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
        });
        thread.setDaemon(true);
        thread.start();

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
        long startTime = System.nanoTime();
        String f = Objects.requireNonNull(getClass().getResource(fileName)).getFile();
        f = URLDecoder.decode(f, UTF_8);

        try (DataInputStream stream = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f)))){

            byte[] bytes = new byte[RawMessage.LENGTH];
            while (stream.available() > 0) {
                long timeStampNs = stream.readLong();
                int bytesRead = stream.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                long timeLapseMs = Duration.ofNanos((startTime + timeStampNs) - System.nanoTime()).toMillis();
                if (timeLapseMs > 0) Thread.sleep(timeLapseMs);
                messageQueue.add(new RawMessage(timeStampNs, message));
            }
        }catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
