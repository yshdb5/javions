package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    private static final int TILE_WIDTH = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Canvas canvas;
    private final Pane pane;
    private GraphicsContext graphicsContext;
    private boolean redrawNeeded;
    private Point2D lastMousePosition;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        redrawNeeded = false;

        this.pane = new Pane(canvas);
        bindPaneToCanvas();

        addListeners();
        creatEventHandlers();
    }

    public Pane pane() {
        return pane;
    }

    public void centerOn(GeoPos pos) {

    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int x0 = getTileIndex(mapParameters.getMinX());
        int y0 = getTileIndex(mapParameters.getMinY());
        int zoom = mapParameters.getZoom();

        double maxX = getTileIndex(mapParameters.getMinX() + canvas.getWidth());
        double maxY = getTileIndex(mapParameters.getMinY() + canvas.getWidth());

        for(int x = x0, a = 0; x < maxX; x++, a += TILE_WIDTH) {
            for (int y = y0, b = 0; y < maxY; y++, b += TILE_WIDTH) {
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

    private void creatEventHandlers()
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
        });

        DoubleProperty lastX = new SimpleDoubleProperty();
        DoubleProperty lastY = new SimpleDoubleProperty();

        pane.setOnMousePressed(e -> {
            lastX.set(e.getX());
            lastY.set(e.getY());
        });
        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(lastX.get() - e.getX(),lastY.get() - e.getY());

            lastX.set(e.getX());
            lastY.set(e.getY());
        });
    }

    private void bindPaneToCanvas() {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }

    private void addListeners() {
        canvas.sceneProperty().addListener((p, oldScene, newScene) -> {
            assert oldScene == null;
            newScene.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        mapParameters.minXProperty().addListener(e -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener(e -> redrawOnNextPulse());
        mapParameters.zoomProperty().addListener(e -> redrawOnNextPulse());

        canvas.widthProperty().addListener(e -> redrawOnNextPulse());
        canvas.heightProperty().addListener(e -> redrawOnNextPulse());
    }

    private int getTileIndex(double pos) {
        return (int) Math.rint(pos / (TILE_WIDTH));
    }
}