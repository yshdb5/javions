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
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * The main class of the application.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class Main extends Application {
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final int INITIAL_ZOOM = 8;
    private static final int X = 33_530;
    private static final int Y = 23_070;
    private final ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    /**
     * This method simply calls the launch method with the command line arguments.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the application by building the scene graph corresponding to the graphical interface,
     * starting the execution thread in charge of getting the messages, and starting the "animation timer"
     * in charge of updating the aircraft states according to the received messages.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception            if something goes wrong.
     * @throws UncheckedIOException in case of input/output error.
     */
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
                tableController.pane(), lineController.pane(), null, null, null);

        SplitPane splitPane = new SplitPane(stackPane, borderPane);
        splitPane.orientationProperty().set(Orientation.VERTICAL);

        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.show();

        Thread thread = new Thread(() -> {
            List<String> args = getParameters().getRaw();
            try {
                if (args.isEmpty()) readFromSystemIn();
                else readAllMessages(args.get(0));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();

        new AnimationTimer() {
            long lastPurge;

            /**
             * This method will be called once per frame.
             * @param now The timestamp of the current frame given in nanoseconds. This
             *            value will be the same for all {@code AnimationTimers} called
             *            during one frame.
             * @throws UncheckedIOException in case of input/output error.
             */
            @Override
            public void handle(long now) {
                try {
                    while (!messageQueue.isEmpty()) {
                        stateManager.updateWithMessage(messageQueue.poll());
                        messageCountProperty.set(messageCountProperty.get() + 1);
                    }
                    if (now - lastPurge >= Duration.ofSeconds(1).toNanos()) stateManager.purge();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }

    /**
     * Reads all messages from a file and adds them to the message queue.
     *
     * @param fileName the name of the file from which to read messages.
     * @throws IOException if the thread is interrupted while sleeping.
     */
    private void readAllMessages(String fileName) throws IOException {
        long startTime = System.nanoTime();

        try (DataInputStream stream = new DataInputStream(
                new BufferedInputStream(new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];

            while (stream.available() > 0) {
                long timeStampNs = stream.readLong();
                int bytesRead = stream.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);

                long timeLapseMs = Duration.ofNanos((startTime + timeStampNs) - System.nanoTime()).toMillis();
                if (timeLapseMs > 0)//noinspection BusyWait
                    Thread.sleep(timeLapseMs);

                Message parsedMessage = MessageParser.parse(new RawMessage(timeStampNs, message));
                if (parsedMessage != null) messageQueue.add(parsedMessage);
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    /**
     * Reads all messages from the standard input (System.in) and adds them to the message queue.
     *
     * @throws IOException if an input or output exception occurred
     */
    private void readFromSystemIn() throws IOException {
        AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                RawMessage rawMessage = demodulator.nextMessage();
                if (rawMessage != null) {
                    Message message = MessageParser.parse(rawMessage);
                    if (message != null) messageQueue.add(message);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
