package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    private static final int TILE_SIZE = 256;
    private final TileManager tileManager;
    private MapParameters mapParameters;
    private final Canvas canvas;
    private final Pane pane;
    private GraphicsContext graphicsContext;
    private boolean redrawNeeded;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        redrawNeeded = false;

        this.pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        manageScroll();
        manageZoom();

        mapParameters.minXProperty().addListener(e -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener(e -> redrawOnNextPulse());
        mapParameters.zoomProperty().addListener(e -> redrawOnNextPulse());

        canvas.widthProperty().addListener(e -> redrawOnNextPulse());
        canvas.heightProperty().addListener(e -> redrawOnNextPulse());
    }

    public Pane pane() {
        return pane;
    }

    public void centerOn(GeoPos pos) {
        // … à faire : mise à jour de la carte;
        redrawOnNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int X = (int) Math.rint(mapParameters.getMinX() / TILE_SIZE);
        int Y = (int) Math.rint(mapParameters.getMinY() / TILE_SIZE);
        int zoom = mapParameters.getZoom();

        try {
            graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoom, X, Y)), 0, 0);
        }
        catch (IOException ignored) {}
        double maxX = (mapParameters.getMinX() + canvas.getWidth()) / TILE_SIZE;
        double maxY = (mapParameters.getMinY() + canvas.getWidth()) / TILE_SIZE;

        for(int x = X, a = 0; x < maxX; x ++, a += TILE_SIZE) {
            for (int y = Y, b = 0; y < maxY; y ++, b += TILE_SIZE) {
                try{
                    graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoom, x, y)), a, b);
                }
                catch (IOException ignored) {}
            }
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void manageScroll()
    {
        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(e.getX(), e.getY());
            redrawOnNextPulse();
        });
    }

    private void manageZoom()
    {
        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            mapParameters.scroll(e.getX(), e.getY());
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-e.getX(), -e.getY());
            redrawOnNextPulse();
        });
    }
}