package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

public final class MapParameters {
    private final static int MIN_ZOOM = 6;
    private final static int MAX_ZOOM = 19;
    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(MIN_ZOOM <= zoom && zoom <= MAX_ZOOM);

        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    public void scroll(double deltaX, double deltaY) {
        minX.set(minX.get() + deltaX);
        minY.set(minY.get() + deltaY);
    }

    public void changeZoomLevel(int deltaZoom) {
        int oldZoom = zoom.get();
        zoom.set(Math2.clamp(MIN_ZOOM, zoom.get() + deltaZoom, MAX_ZOOM));
        int newZoom = zoom.get();
        if (oldZoom != newZoom) {
            minX.set(minX.get() * (1 << deltaZoom));
            minY.set(minY.get() * (1 << deltaZoom));
        }
    }

    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    public int getZoom() {
        return zoom.get();
    }

    public ReadOnlyDoubleProperty minXProperty() {
        return minX;
    }

    public double getMinX() {
        return minX.get();
    }

    public ReadOnlyDoubleProperty minYProperty() {
        return minY;
    }

    public double getMinY() {
        return minY.get();
    }
}
