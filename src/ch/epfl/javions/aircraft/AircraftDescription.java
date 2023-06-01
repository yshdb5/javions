package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Record AircraftDescription : represents the aircraft description.
 *
 * @param string : the string that represents the aircraft description.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftDescription(String string) {
    /**
     * Pattern that represents the aircraft description.
     */
    private static final Pattern descriptionPattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * compact constructor : valids the string given in argument, checks if it corresponds to an aircraft description pattern
     * the string can be empty
     *
     * @throws IllegalArgumentException if it doesn't match with the pattern
     */
    public AircraftDescription {
        Preconditions.checkArgument(descriptionPattern.matcher(string).matches() || string.isEmpty());
    }
}
