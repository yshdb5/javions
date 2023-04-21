package ch.epfl.javions.gui;


import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager
{
    public record TileId(int zoom, int x, int y)
    {
        public static boolean isValid(int zoom, int x, int y)
        {
            int maxIndex = 1 << zoom;
            return (0 <= x && x < maxIndex) && (0 <= y && y < maxIndex);
        }
    }
    private final static int MAX_CAPACITY = 100;
    private final Path cachePath;
    private final String serverName;
    private final Map<TileId, Image> cacheMap;

    public TileManager (Path filePath, String serverName)
    {
        this.cachePath = filePath;
        this.serverName = serverName;
        this.cacheMap = new LinkedHashMap<>(MAX_CAPACITY, 0.75f, true) {};
    }

    public Image imageForTileAt (TileId tileId) throws IOException {
        if (cacheMap.containsKey(tileId))
        {
            return cacheMap.get(tileId);
        }
        else if (Files.exists(pathOf(tileId))) {
            try (FileInputStream i = new FileInputStream(pathOf(tileId).toFile())) {
                    Image image = new Image(i);
                    cacheMap.put(tileId, image);
                    return image;
            }
        }
        else{
            URL u = new URL("https://" + serverName + "/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Javions");

            try (InputStream i = c.getInputStream();
                 FileOutputStream o = new FileOutputStream(pathOf(tileId).toFile())) {
                 byte [] bytes = i.readAllBytes();
                 Files.createDirectories(pathOf(tileId).getParent());

                 o.write(bytes);
                 ByteArrayInputStream b = new ByteArrayInputStream(bytes);
                 Image image = new Image(b);
                 cacheMap.put(tileId, image);
                 return image;
            }
        }
    }

    private Path pathOf(TileId tileId) {
        return cachePath.resolve(tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png");
    }
}
