package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Final BaseMapController class : manages the display and interaction with the background map.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class BaseMapController {
    private static final int TILE_WIDTH = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Canvas canvas;
    private final Pane pane;
    private Point2D lastMousePos;
    private boolean redrawNeeded;

    /**
     * BaseMapController's constructor.
     *
     * @param tileManager   the tile manager to be used to get the tiles from the map.
     * @param mapParameters the parameters of the visible portion of the map.
     */
    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        canvas = new Canvas();
        pane = new Pane(canvas);
        redrawNeeded = false;
        lastMousePos = null;

        bindPaneToCanvas();
        addListeners();
        creatEventHandlers();
    }

    /**
     * @return the JavaFX panel displaying the background map
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Moves the visible portion of the map so that it is centered at the position given.
     *
     * @param pos a point on the surface of the Earth
     */
    public void centerOn(GeoPos pos) {
        double x = WebMercator.x(mapParameters.getZoom(), pos.longitude());
        double y = WebMercator.y(mapParameters.getZoom(), pos.latitude());

        double deltaX = x - (mapParameters.getMinX() + canvas.getWidth() / 2);
        double deltaY = y - (mapParameters.getMinY() + canvas.getHeight() / 2);
        mapParameters.scroll(deltaX, deltaY);
    }

    /**
     * Forces a redraw of the map if the redrawNeeded flag is set to true.
     * This method will clear the canvas and redraw tiles within the visible
     * range of the map. The tiles are fetched from the TileManager and drawn
     * onto the canvas.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double minX = mapParameters.getMinX();
        double minY = mapParameters.getMinY();

        int x0 = getTileIndex(minX);
        int y0 = getTileIndex(minY);
        int zoom = mapParameters.getZoom();

        double maxX = getTileIndex(minX + canvas.getWidth());
        double maxY = getTileIndex(minY + canvas.getHeight());

        for (int x = x0; x <= maxX; x++) {
            for (int y = y0; y <= maxY; y++) {
                try {
                    if (!TileManager.TileId.isValid(zoom, x, y)) continue;
                    graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoom, x, y)),
                            x * TILE_WIDTH - minX, y * TILE_WIDTH - minY);
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Signals that a redraw is required during the next pulse.
     * A pulse is a moment when all changes on the scene graph state
     * are processed. Requesting a pulse triggers processing of
     * these changes during which a redraw of the map can be initiated.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Creates event handlers for scrolling and mouse dragging.
     * The scrolling event handler allows zooming in and out of the map,
     * while the mouse dragging event handler allows the map to be panned.
     */
    private void creatEventHandlers() {
        LongProperty minScrollTime = new SimpleLongProperty();

        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            double x = e.getX();
            double y = e.getY();

            mapParameters.scroll(x, y);
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-x, -y);
        });

        pane.setOnMousePressed(e -> lastMousePos = new Point2D(e.getX(), e.getY()));

        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(lastMousePos.getX() - e.getX(), lastMousePos.getY() - e.getY());

            lastMousePos = new Point2D(e.getX(), e.getY());
        });
    }

    /**
     * Binds the dimensions of the canvas to those of the pane.
     * This ensures that the canvas always fits the pane when its size changes.
     */
    private void bindPaneToCanvas() {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }

    /**
     * Adds listeners to the properties of the mapParameters and canvas.
     * These listeners trigger a redraw of the map when any of the observed
     * properties changes.
     */
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

    /**
     * Calculates and returns the tile index for a given position.
     * The tile index determines the tile that contains the given position.
     *
     * @param pos the position.
     * @return the index of the tile containing the position.
     */
    private int getTileIndex(double pos) {
        return (int) Math.floor(pos / (TILE_WIDTH));
    }
}