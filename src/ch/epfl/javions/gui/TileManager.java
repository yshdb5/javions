package ch.epfl.javions.gui;

public final class TileManager
{
    public record TileId(int zoom, int x, int y)
    {
        public static boolean isValid(int zoom, int x, int y)
        {
            return true;
        }
    }
}
