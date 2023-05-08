package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.sun.javafx.scene.control.skin.Utils.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class Main extends Application {
    private ConcurrentLinkedQueue<RawMessage> messageQueue;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Path tileCache = Path.of("tile-cache");
        TileManager tileManager = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mapParameters = new MapParameters(8, 33_530, 23_070);
        BaseMapController mapController = new BaseMapController(tileManager, mapParameters);

        URL url = getClass().getResource("/aircraft.zip");
        assert url != null;
        Path path = Path.of(url.toURI());

        AircraftDatabase database = new AircraftDatabase(path.toString());
        AircraftStateManager stateManager = new AircraftStateManager(database);
        ObservableSet<ObservableAircraftState> observableAircraftSet = stateManager.states();
        ObjectProperty<ObservableAircraftState> aircraftStateProperty = new SimpleObjectProperty<>();

        AircraftController controller =
                new AircraftController(mapParameters, observableAircraftSet, aircraftStateProperty);
        AircraftTableController tableController =
                new AircraftTableController(observableAircraftSet, aircraftStateProperty);

        StatusLineController lineController = new StatusLineController();
        lineController.aircraftCountProperty().bind(Bindings.size(observableAircraftSet));

        StackPane stackPane = new StackPane(mapController.pane(), controller.pane());
        BorderPane borderPane = new BorderPane(
                tableController.pane(), lineController.pane(), null, null,  null);

        SplitPane splitPane = new SplitPane(stackPane, borderPane);

        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.show();

        new Thread(() -> {
            messageQueue = new ConcurrentLinkedQueue<>();
            List<String> args = getParameters().getRaw();
            if(args.isEmpty())
            {

            }
            else
            {
                messageQueue.addAll(readAllMessages(args.get(0)));
            }
        });

        Iterator<RawMessage> messageIterator = messageQueue.iterator();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; i += 1) {
                        if (!messageIterator.hasNext()) return;
                        Message message = MessageParser.parse(messageIterator.next());
                        if (message != null) stateManager.updateWithMessage(message);
                        stateManager.purge();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }

    private static List<RawMessage> readAllMessages(String fileName){
        List<RawMessage> messageList = new ArrayList<>();
        String f = getResource(fileName).getFile();
        f = URLDecoder.decode(f, UTF_8);

        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f)))){

            byte[] bytes = new byte[RawMessage.LENGTH];
            while (s.available() > 0) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                messageList.add(new RawMessage(timeStampNs, message));
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return messageList;
    }
}
