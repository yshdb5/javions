package ch.epfl.javions;
/**
 * record Geopos : represents the geographical coordinates
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public record GeoPos (int longitudeT32, int latitudeT32 )
{
    /**
     * Compact constructor of GeoPos
     * Checks if the latitude given is valid
     *
     * @throws IllegalArgumentException
     *
     */
    public GeoPos
    {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }

    /**
     * checks if the latitude is in between -2 power 30 and 2 power 30
     * @param latitudeT32
     *       the latitude we need to check
     *
     * @return true if the latitude is in between -2 power 30 and 2 power 30
     */
    public static boolean isValidLatitudeT32(int latitudeT32)
    {
        return ((latitudeT32 >= -Math.scalb(1d, 30)) && (latitudeT32 <= Math.scalb(1d, 30)));
    }

    /**
     * converts the longitudes
     *
     * @return the longitudes in radian
     */
    public double longitude()
    {
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }

    /**
     * converts the latitude
     *
     * @return the longitudes in radian
     */
    public double latitude()
    {
        return Units.convertFrom(latitudeT32, Units.Angle.T32);
    }

    /**
     * redefinition of toString
     * @return a textual representation of the coordinates (longitude,latitude)
     */
    @Override
    public String toString()
    {
        return "(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°, "
                + Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)";
    }
}