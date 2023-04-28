package ch.epfl.javions.adsb;


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * class CprDecoder : contains methods to extract a subset of the 64 bits of a long type value
 * represents a CPR position decoder
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class CprDecoder {
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

        int zonePhi = (int) Math.rint(y0 * Z1 - y1 * Z0);
        int zonePhi0 = (zonePhi < 0) ? zonePhi + Z0 : zonePhi;
        int zonePhi1 = (zonePhi < 0) ? zonePhi + Z1 : zonePhi;

        double latitude0Turn = recenterPosition(DELTA0 * (zonePhi0 + y0));
        double latitude1Turn = recenterPosition(DELTA1 * (zonePhi1 + y1));

        int latitude0T32 = convertTurnToT32(latitude0Turn);
        int latitude1T32 = convertTurnToT32(latitude1Turn);

        if ((mostRecent == 0) && !GeoPos.isValidLatitudeT32(latitude0T32)
                || (mostRecent == 1) && !GeoPos.isValidLatitudeT32(latitude1T32)) return null;


        double a0 = aOf(latitude0Turn);
        double a1 = aOf(latitude1Turn);

        int zoneNumber00 = zoneNumberOf(a0);
        int zoneNumber01 = zoneNumberOf(a1);

        if (zoneNumber00 != zoneNumber01) return null;

        int zoneNumber1 = zoneNumber00 - 1;

        int zoneLambda = (int) Math.rint(x0 * zoneNumber1 - x1 * zoneNumber00);
        int zoneLambda0 = (zoneLambda < 0) ? zoneLambda + zoneNumber00 : zoneLambda;
        int zoneLambda1 = (zoneLambda < 0) ? zoneLambda + zoneNumber1 : zoneLambda;

        double deltaLambda0 = calculateDeltaLambda(zoneNumber00);
        double deltaLambda1 = calculateDeltaLambda(zoneNumber1);

        double longitude0Turn = calculateLongitudeTurn(zoneNumber00, x0, deltaLambda0, zoneLambda0);
        double longitude1Turn = calculateLongitudeTurn(zoneNumber00, x1, deltaLambda1, zoneLambda1);

        int longitude0T32 = convertTurnToT32(longitude0Turn);
        int longitude1T32 = convertTurnToT32(longitude1Turn);

        return (mostRecent == 0) ? new GeoPos(longitude0T32, latitude0T32) : new GeoPos(longitude1T32, latitude1T32);
    }

    private static double aOf(double latitude_TURN) {
        double angle_rad = Math.cos(Units.convertFrom(latitude_TURN, Units.Angle.TURN)) * Math.cos(Units.convertFrom(latitude_TURN, Units.Angle.TURN));
        return 1 - ((1 - Math.cos(Units.Angle.TURN * DELTA0)) / (angle_rad));
    }

    private static int zoneNumberOf(double A) {
        if (Math.abs(A) > 1) return 1;
        else return (int) Math.floor(Units.Angle.TURN / Math.acos(A));
    }

    private static double recenterPosition(double latitudeOrLongitude_TURN) {
        if (latitudeOrLongitude_TURN >= 0.5) return latitudeOrLongitude_TURN - 1;
        else return latitudeOrLongitude_TURN;
    }

    private static int convertTurnToT32(double latitudeOrLongitude_TURN) {
        return (int) Math.rint(Units.convert(latitudeOrLongitude_TURN, Units.Angle.TURN, Units.Angle.T32));
    }

    private static double calculateDeltaLambda(int zoneNumber) {
        return ((double) 1) / zoneNumber;
    }

    private static double calculateLongitudeTurn(int zoneNumber0, double x, double deltaLambda, int zoneLambda) {
        return (zoneNumber0 == 1) ?
                recenterPosition(x) :
                recenterPosition(deltaLambda * (zoneLambda + x));
    }
}
