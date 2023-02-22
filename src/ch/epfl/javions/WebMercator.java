package ch.epfl.javions;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/02/23
 */

public class WebMercator
{
    private WebMercator(){}

    public static double x (int zoomLevel, double longitude)
    {
        return Math.scalb(1, 8 + zoomLevel)*(longitude/(2*Math.PI + 0.5));
    }

    public static double y(int zoomLevel, double latitude)
    {
        return Math.scalb(1, 8 + zoomLevel)*((-Math2.asinh((Math.tan(latitude)))/(2*Math.PI)) + 0.5);
    }
}
