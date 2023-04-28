package ch.epfl.javions;

/**
 * class WebMercator : allows to project the coordinates based on the WebMercator projection
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class WebMercator {
    private static final double OFFSET = 0.5;
    private static final int POWER = 8;

    private WebMercator() {
    }

    /**
     * static method that calculates x (after the zoom) thanks to the formula given
     *
     * @param zoomLevel zoom level on the map
     * @param longitude given longitude (in radians)
     * @return the x coordinate that corresponds to the longitude on the zoom
     */
    public static double x(int zoomLevel, double longitude) {
        return calculateCoordinate(longitude, zoomLevel);
    }

    /**
     * static method that calculates y (after the zoom) thanks to the formula given
     *
     * @param zoomLevel zoom level on the map
     * @param latitude  given latitude (in radians)
     * @return the y coordinate that corresponds to the latitude on the zoom
     */

    public static double y(int zoomLevel, double latitude) {
        return calculateCoordinate(-Math2.asinh(Math.tan(latitude)), zoomLevel);
    }

    private static double calculateCoordinate(double value, int zoomLevel){
        return Math.scalb(Units.convertTo(value, Units.Angle.TURN) + OFFSET, POWER + zoomLevel);
    }
}
