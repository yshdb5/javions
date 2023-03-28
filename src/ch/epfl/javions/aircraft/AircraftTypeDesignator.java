package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * record AircraftDesignator : represents an aircraft designator
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftTypeDesignator(String string) {
    private static final Pattern typePattern = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * compact constructor : valids the string given in argument, checks if it corresponds to an aircraft designator pattern
     * the string can be empty
     *
     * @throws IllegalArgumentException
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument(typePattern.matcher(string).matches() || string.isEmpty());
    }
}
