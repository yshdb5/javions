package ch.epfl.javions.gui;


import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

public final class TileManager
{
    private static final int MIN_ZOOM = 0;
    private static final int MAX_ZOOM = 19;
    private final Path filePath;
    private final String serverName;
    public TileManager (Path filePath, String serverName)
    {
        this.filePath = filePath;
        this.serverName = serverName;
    }

    public Image imageForTileAt (TileId tileId) throws IOException {

        URL u = new URL("https://tile.openstreetmap.org/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");
        InputStream i = c.getInputStream();

        return new Image(i);
    }
    public record TileId(int zoom, int x, int y)
    {
        public static boolean isValid(int zoom, int x, int y)
        {
            return (MIN_ZOOM <= zoom && zoom <= MAX_ZOOM);
        }
    }
}
