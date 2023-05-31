package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is a tile manager for the map from OpenStreetMap (OSM).
 * It handles the loading of tiles from the server or from a disk cache.
 * It also uses an in-memory cache to reduce the latency of repeated requests.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class TileManager {

    /**
     * Final TileManager record : represents the identity of an OSM tile.
     *
     * @param zoom the zoom level of the tile.
     * @param x    the X index of the tile.
     * @param y    the Y index of the tile.
     */
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

    /**
     * TileManager's constructor.
     *
     * @param filePath   the path to the folder containing the disk cache.
     * @param serverName the name of the tile server.
     */
    public TileManager(Path filePath, String serverName) {
        this.cachePath = filePath;
        this.serverName = serverName;
        this.cacheMap = new LinkedHashMap<>(MAX_CAPACITY, DEFAULT_LOAD_FACTOR, true) {};
    }

    /**
     * This method takes the identity of a tile and returns its image.
     * First, it checks if the image is in the cache.
     * If not, it checks if the image file exists on the disk.
     * If it doesn't, it downloads the image from the server.
     *
     * @param tileId the identity of the tile.
     * @return the image of the tile.
     * @throws IOException if an I/O error occurs.
     */
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

    /**
     * This method constructs the path of a tile image file based on its identity.
     *
     * @param tileId the identity of the tile.
     * @return the path of the tile image file.
     */
    private Path pathOf(TileId tileId) {
        return cachePath.resolve(tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png");
    }

    /**
     * This method loads a tile image from a file.
     * It opens the file, reads the image, puts the image into the cache, and then returns it.
     *
     * @param tileId the identity of the tile.
     * @param path   the path of the tile image file.
     * @return the image of the tile.
     * @throws IOException if an I/O error occurs.
     */
    private Image loadImageFromFile(TileId tileId, Path path) throws IOException {
        try (InputStream i = Files.newInputStream(path)) {
            Image image = new Image(i);
            cacheMap.put(tileId, image);
            return image;
        }
    }

    /**
     * This method loads a tile image from the server.
     * It opens a connection to the server, reads the image,
     * writes it to the disk cache, puts the image into the cache, and then returns it.
     *
     * @param tileId the identity of the tile.
     * @param path   the path of the tile image file.
     * @return the image of the tile.
     * @throws IOException if an I/O error occurs.
     */
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
