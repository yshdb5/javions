package ch.epfl.javions.gui;


import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager {
    public record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            int maxIndex = 1 << zoom;
            return (0 <= x && x < maxIndex) && (0 <= y && y < maxIndex);
        }
    }
    private final static int MAX_CAPACITY = 100;
    private final static float DEFAULT_LOAD_FACTOR = 0.75f;
    private final Path cachePath;
    private final String serverName;
    private final Map<TileId, Image> cacheMap;
    public TileManager(Path filePath, String serverName) {
        this.cachePath = filePath;
        this.serverName = serverName;
        this.cacheMap = new LinkedHashMap<>(MAX_CAPACITY, DEFAULT_LOAD_FACTOR, true) {};
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        Path path = pathOf(tileId);

        if (cacheMap.containsKey(tileId)) {
            return cacheMap.get(tileId);
        } else if (Files.exists(path)) {
            return loadImageFromFile(tileId, path);
        } else {
            return loadImageFromServer(tileId, path);
        }
    }

    private Path pathOf(TileId tileId) {
        return cachePath.resolve(tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png");
    }

    private Image loadImageFromFile(TileId tileId, Path path) throws IOException {
        try (InputStream i = Files.newInputStream(path)) {
            Image image = new Image(i);
            cacheMap.put(tileId, image);
            return image;
        }
    }

    private Image loadImageFromServer(TileId tileId, Path path) throws IOException {
        URL u = new URL("https://" + serverName + "/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");

        Files.createDirectories(path.getParent());

        try (InputStream i = c.getInputStream()) {
            byte[] bytes = i.readAllBytes();
            Files.write(path, bytes);
            ByteArrayInputStream b = new ByteArrayInputStream(bytes);
            Image image = new Image(b);
            cacheMap.put(tileId, image);
            return image;
        }
    }
}
