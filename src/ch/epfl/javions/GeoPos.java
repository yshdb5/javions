package ch.epfl.javions;/*
 *	Author:      Yshaï Dinée-Baumgarten
 *	Date:        21/02/23
 */

public record GeoPos (int longitudeT32, int latitudeT32 )
{
    public static boolean isValidLatitudeT32(int latitudeT32)
    {
        if (!((latitudeT32 > -Math.pow(2, 30)) && (latitudeT32 < Math.pow(2, 30))))
        {
            throw new IllegalArgumentException();
        }
        else
        {
            return true;
        }
    }

    public double longitude()
    {
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }

    public double latitude()
    {
        return Units.convertFrom(latitudeT32, Units.Angle.T32);
    }

    @Override
    public String toString()
    {
        return "(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + " °, "
                + Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + " °) ";
    }
}