package ch.epfl.javions;/*
 /**
 * class Math2 : define computing functions.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class Math2
{

    private Math2(){}

    /**
     * limits the v - value to the interval from min to max
     *
     * @param min
     *        lower range
     * @param v
     *        v-value
     * @param max
     *        upper range
     * @throws IllegalArgumentException
     *        if the minimum is greater than the maximum
     *
     * @return the v -value when it is included in the interval
     *
     */
    public static int clamp(int min, int v, int max)
    {
        if (min > max)
        {
            throw new IllegalArgumentException();
        }

        if (v < min)
        {
            return min;
        }
        else if (v > max)
        {
            return max;
        }
        else
        {
            return v;
        }
    }

    /**
     * Allows you to calculate the reciprocal hyperbolic sine
     *
     * @param x
     *       x is a real value
     * @return
     *       applies the reciprocal hyperbolic sine formula to x
     */
    public static double asinh(double x)
    {
       return Math.log(x + Math.sqrt(1 + x*x));
    }
}
