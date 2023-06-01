package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Record AircraftDesignator : represents an aircraft designator
 *
 * @param string : the string representing the designator.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftTypeDesignator(String string) {
    /**
     * Pattern of an aircraft designator.
     */
    private static final Pattern typePattern = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * Compact constructor : validates the string given in argument,
     * checks if it corresponds to an aircraft designator pattern.
     * The string can be empty.
     *
     * @throws IllegalArgumentException if it doesn't match with the pattern
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument(typePattern.matcher(string).matches() || string.isEmpty());
    }
}
