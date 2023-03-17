package ch.epfl.javions.adsb;

/**
 * class CprDecoder : contains methods to extract a subset of the 64 bits of a long type value
 * represents a CPR position decoder
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder
{
    private static final int Z0 = 60;
    private static final int Z1 = 59;
    private static final double DELTA0 = ( 1.0)/Z0;
    private static final double DELTA1 = ( 1.0)/Z1;
    private CprDecoder(){}

    /**
     *
     * @param x0
     *        global longitude of an even message
     * @param y0
     *        global latitude of an even message
     * @param x1
     *        global longitude of an odd message
     * @param y1
     *        global latitude of an even message
     * @param mostRecent
     *        the most recent positions (0 or 1)
     * @throws IllegalArgumentException
     *        if mostRecent is not 0 or 1
     * @return the geographical position corresponding to the given normalized local positions
     *         or null if the latitude of the decoded position is not valid (i.e. within ±90°)
     *

     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent)
    {
        Preconditions.checkArgument((mostRecent == 1) || (mostRecent == 0));
        double latitude0_TURN, latitude1_TURN, longitude0_TURN, longitude1_TURN, A, deltaLambda0, deltaLambda1;
        int latitude0_T32, latitude1_T32, longitude0_T32, longitude1_T32, zonePhi, zonePhi0, zonePhi1, zoneLambda, zoneLambda0, zoneLambda1,  nombreZones0, nombreZones1;

        y0 = y0*Math.scalb(1, -17);
        y1 = y1*Math.scalb(1, -17);

        zonePhi = (int) Math.rint(y0*Z1 - y1*Z0);


        if (zonePhi < 0)
        {
            zonePhi0 = zonePhi + Z0;
            zonePhi1 = zonePhi + Z1;
        }
        else
        {
            zonePhi0 = zonePhi;
            zonePhi1 = zonePhi;
        }

        latitude0_TURN = DELTA0*(zonePhi0 + y0);
        latitude1_TURN = DELTA1*(zonePhi1 + y1);

        if (latitude0_TURN >= 0.5)
        {
            latitude0_TURN -= 1;
        }
        if (latitude1_TURN >= 0.5)
        {
            latitude1_TURN -= 1;
        }

        latitude0_T32 = (int) Math.rint(Units.convert(latitude0_TURN, Units.Angle.TURN, Units.Angle.T32));
        latitude1_T32 = (int) Math.rint(Units.convert(latitude1_TURN, Units.Angle.TURN, Units.Angle.T32));

        double Angle_Rad = Math.cos(Units.convert(latitude0_TURN,Units.Angle.TURN,Units.Angle.RADIAN)) * Math.cos(Units.convert(latitude0_TURN,Units.Angle.TURN,Units.Angle.RADIAN));
        A = 1 - ((1-Math.cos(Math.PI*2*DELTA0)) / (Angle_Rad));
        if (A > 1)
        {
            nombreZones0 = 1;
        }
        else
        {
            A = Math.acos(A);
            nombreZones0 = (int) Math.floor((Math.PI*2.0)/A);
        }

        nombreZones1 = nombreZones0 -1;

        x0 = x0*Math.scalb(1, -17);
        x1 = x1*Math.scalb(1, -17);

        zoneLambda = (int) Math.rint(x0*nombreZones1 - x1*nombreZones0);

        if (zoneLambda < 0)
        {
            zoneLambda0 = zoneLambda + nombreZones0;
            zoneLambda1 = zoneLambda + nombreZones1;
        }
        else
        {
            zoneLambda0 = zoneLambda;
            zoneLambda1 = zoneLambda;
        }

        deltaLambda0 = ((double) 1) / nombreZones0;
        deltaLambda1 = ((double) 1) / nombreZones1;

        longitude0_TURN = deltaLambda0*(zoneLambda0 + x0);
        longitude1_TURN = deltaLambda1*(zoneLambda1 + x1);

        if (longitude0_TURN >= 0.5)
        {
            longitude0_TURN -= 1;
        }
        if (longitude1_TURN >= 0.5)
        {
            longitude1_TURN -= 1;
        }

        longitude0_T32 = (int) Math.rint(Units.convert(longitude0_TURN, Units.Angle.TURN, Units.Angle.T32));
        longitude1_T32 = (int) Math.rint(Units.convert(longitude1_TURN, Units.Angle.TURN, Units.Angle.T32));

        if (mostRecent == 0)
        {
            return new GeoPos(longitude0_T32, latitude0_T32);
        }
        else
        {
            return new GeoPos(longitude1_T32, latitude1_T32);
        }
    }
}
