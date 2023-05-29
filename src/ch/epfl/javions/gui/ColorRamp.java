package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Final immutable ColorRamp class : represents a gradient of colors.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public final class ColorRamp {
    /**
     * The constant that defines the Plasma gradient.
     */
    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));
    private final List<Color> colorList;
    private final double interval;

    /**
     * private ColorRamp's constructor : accepts as argument a JavaFX color sequence, of type Color
     *
     * @param color the color sequence.
     */
    private ColorRamp(Color... color) {
        Preconditions.checkArgument(color.length >= 2);

        colorList = List.of(color);
        interval = ((double) 1) / (colorList.size() - 1);
    }


    /**
     * @param index the index of the colour.
     * @return the color corresponding to the index mixed with the colour of the next index.
     */
    public Color at(double index) {
        if (index < 0) return colorList.get(0);
        else if (index > 1) return colorList.get(colorList.size() - 1);

        int i = (int) (index / interval);
        double proportion = (index - i * interval) % interval / interval;
        return colorList.get(i).interpolate(colorList.get(i + 1), proportion);
    }
}
