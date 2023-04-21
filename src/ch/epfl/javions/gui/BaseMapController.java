package ch.epfl.javions.gui;


import ch.epfl.javions.GeoPos;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController
{
    private static final int TILE_SIZE = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private Canvas canvas;
    private Pane pane;
    private GraphicsContext graphicsContext;
    private boolean redrawNeeded;
    public BaseMapController(TileManager tileManager, MapParameters mapParameters) throws IOException {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        redrawNeeded = false;

        this.pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());

        this.graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(tileManager.imageForTileAt(tileId(mapParameters)), 0, 0);

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            // … à faire : appeler les méthodes de MapParameters
        });
    }

    public Pane pane() {
        return pane;
    }

    public void centerOn(GeoPos pos)
    {
        // … à faire : mise à jour de la carte;
        redrawOnNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // … à faire : dessin de la carte
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private static TileManager.TileId tileId(MapParameters mapParameters)
    {
        int X = (int) Math.rint(mapParameters.getMinX() / TILE_SIZE);
        int Y = (int) Math.rint(mapParameters.getMinY() / TILE_SIZE);
        return new TileManager.TileId(mapParameters.getZoom(), X, Y);
    }
}
