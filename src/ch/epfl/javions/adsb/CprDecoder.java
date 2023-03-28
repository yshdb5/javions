package ch.epfl.javions.adsb;

/**
 * class CprDecoder : contains methods to extract a subset of the 64 bits of a long type value
 * represents a CPR position decoder
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    private static final int Z0 = 60;
    private static final int Z1 = 59;
    private static final double DELTA0 = (1.0) / Z0;
    private static final double DELTA1 = (1.0) / Z1;

    private CprDecoder() {
    }

    /**
     * @param x0         global longitude of an even message
     * @param y0         global latitude of an even message
     * @param x1         global longitude of an odd message
     * @param y1         global latitude of an even message
     * @param mostRecent the most recent positions (0 or 1)
     * @return the geographical position corresponding to the given normalized local positions
     * or null if the latitude of the decoded position is not valid (i.e. within ±90°)
     * @throws IllegalArgumentException if mostRecent is not 0 or 1
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument((mostRecent == 1) || (mostRecent == 0));

        double latitude0Turn, latitude1Turn, longitude0Turn, longitude1Turn, a0, a1, deltaLambda0, deltaLambda1;
        int latitude0T32, latitude1T32, longitude0T32, longitude1T32, zonePhi, zonePhi0, zonePhi1, zoneLambda,
                zoneLambda0, zoneLambda1, zoneNumber00, zoneNumber01, zoneNumber1;

        zonePhi = (int) Math.rint(y0 * Z1 - y1 * Z0);

        if (zonePhi < 0) {
            zonePhi0 = zonePhi + Z0;
            zonePhi1 = zonePhi + Z1;
        } else {
            zonePhi0 = zonePhi;
            zonePhi1 = zonePhi;
        }

        latitude0Turn = recenterPosition(DELTA0 * (zonePhi0 + y0));
        latitude1Turn = recenterPosition(DELTA1 * (zonePhi1 + y1));

        latitude0T32 = convertTurnToT32(latitude0Turn);
        latitude1T32 = convertTurnToT32(latitude1Turn);

        if ((mostRecent == 0) && !GeoPos.isValidLatitudeT32(latitude0T32)
                || (mostRecent == 1) && !GeoPos.isValidLatitudeT32(latitude1T32)) {
            return null;
        }

        a0 = aOf(latitude0Turn);
        a1 = aOf(latitude1Turn);

        zoneNumber00 = zoneNumberOf(a0);
        zoneNumber01 = zoneNumberOf(a1);

        if (zoneNumber00 != zoneNumber01) {
            return null;
        }

        zoneNumber1 = zoneNumber00 - 1;

        zoneLambda = (int) Math.rint(x0 * zoneNumber1 - x1 * zoneNumber00);

        if (zoneLambda < 0) {
            zoneLambda0 = zoneLambda + zoneNumber00;
            zoneLambda1 = zoneLambda + zoneNumber1;
        } else {
            zoneLambda0 = zoneLambda;
            zoneLambda1 = zoneLambda;
        }

        deltaLambda0 = ((double) 1) / zoneNumber00;
        deltaLambda1 = ((double) 1) / zoneNumber1;

        if (zoneNumber00 == 1) {
            longitude0Turn = recenterPosition(x0);
            longitude1Turn = recenterPosition(x1);
        } else {
            longitude0Turn = recenterPosition(deltaLambda0 * (zoneLambda0 + x0));
            longitude1Turn = recenterPosition(deltaLambda1 * (zoneLambda1 + x1));
        }

        longitude0T32 = convertTurnToT32(longitude0Turn);
        longitude1T32 = convertTurnToT32(longitude1Turn);

        if (mostRecent == 0) {
            return new GeoPos(longitude0T32, latitude0T32);
        } else {
            return new GeoPos(longitude1T32, latitude1T32);
        }
    }

    private static double aOf(double latitude_TURN) {
        double angle_rad = Math.cos(Units.convertFrom(latitude_TURN, Units.Angle.TURN)) * Math.cos(Units.convertFrom(latitude_TURN, Units.Angle.TURN));
        return 1 - ((1 - Math.cos(Math.PI * 2 * DELTA0)) / (angle_rad));
    }

    private static int zoneNumberOf(double A) {
        if (Math.abs(A) > 1) {
            return 1;
        } else {
            return (int) Math.floor((Math.PI * 2.0) / Math.acos(A));
        }
    }

    private static double recenterPosition(double latitudeOrLongitude_TURN) {
        if (latitudeOrLongitude_TURN >= 0.5) {
            return latitudeOrLongitude_TURN - 1;
        } else {
            return latitudeOrLongitude_TURN;
        }
    }

    private static int convertTurnToT32(double latitudeOrLongitude_TURN) {
        return (int) Math.rint(Units.convert(latitudeOrLongitude_TURN, Units.Angle.TURN, Units.Angle.T32));
    }
}
