package ch.epfl.javions;

/**
 * Class Math2 : define computing functions.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class Math2 {

    private Math2() {
    }

    /**
     * Limits the v - value to the interval from min to max.
     *
     * @param min lower range
     * @param v   v-value
     * @param max upper range
     * @return the v -value when it is included in the interval
     * @throws IllegalArgumentException if the minimum is greater than the maximum
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);

        return Math.max(min, Math.min(v, max));
    }

    /**
     * Allows you to calculate the reciprocal hyperbolic sin.
     *
     * @param x x is a real value
     * @return applies the reciprocal hyperbolic sine formula to x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.hypot(1, x));
    }
}
