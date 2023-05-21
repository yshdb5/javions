package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

/**
 * Final MapParameters class : represents the parameters of the portion of the map visible in the graphical interface
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class MapParameters {
    private final static int MIN_ZOOM = 6;
    private final static int MAX_ZOOM = 19;
    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    /**
     * MapParameters constructor.
     * @param zoom the zoom level.
     * @param minX the x-coordinate of the top-left corner of the visible portion of the map
     * @param minY the y-coordinate of the top-left corner of the visible portion of the map
     */
    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(MIN_ZOOM <= zoom && zoom <= MAX_ZOOM);

        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }


    /**
     * Translates the top-left corner of the displayed map portion of this vector.
     * @param deltaX the x component of a vector
     * @param deltaY the y component of a vector
     */

    public void scroll(double deltaX, double deltaY) {
        minX.set(getMinX()+ deltaX);
        minY.set(getMinY() + deltaY);
    }

    /**
     * Adds delta zoom to the current zoom level.
     * @param deltaZoom difference of zoom level.
     */

    public void changeZoomLevel(int deltaZoom) {
        int oldZoom = getZoom();
        zoom.set(Math2.clamp(MIN_ZOOM, oldZoom + deltaZoom, MAX_ZOOM));
        int newZoom = getZoom();
        if (oldZoom != newZoom) {
            minX.set(Math.scalb(getMinX(), deltaZoom));
            minY.set(Math.scalb(getMinY(), deltaZoom));
        }
    }

    /**
     * Read-only zoom access method.
     * @return the zoom.
     */

    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    /**
     * Method to access the value contained in the zoom.
     * @return the value of the zoom.
     */

    public int getZoom() {
        return zoom.get();
    }

    /**
     * Read-only minX access method.
     * @return the minX coordinate.
     */

    public ReadOnlyDoubleProperty minXProperty() {
        return minX;
    }

    /**
     * Method to access the value of the minX coordinate.
     * @return the value of the minX coordinate.
     */

    public double getMinX() {
        return minX.get();
    }

    /**
     * Read-only minY access method.
     * @return the minY coordinate.
     */

    public ReadOnlyDoubleProperty minYProperty() {
        return minY;
    }

    /**
     * Method to access the value of the minY coordinate.
     * @return the value of the minY coordinate.
     */

    public double getMinY() {
        return minY.get();
    }
}
