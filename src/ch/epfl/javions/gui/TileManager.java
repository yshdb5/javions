package ch.epfl.javions.gui;


public final class TileManager
{
    private static final int MIN_ZOOM = 0;
    private static final int MAX_ZOOM = 19;
    public record TileId(int zoom, int x, int y)
    {
        public static boolean isValid(int zoom, int x, int y)
        {
            return (MIN_ZOOM <= zoom && zoom <= MAX_ZOOM);
        }
    }
}
