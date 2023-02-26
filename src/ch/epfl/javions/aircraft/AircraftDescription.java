package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;
/**
 * record AircraftDescription : represents the aicraft description
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftDescription (String string)
{
    private static Pattern descriptionPattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * compact constuctor : valids the string given in argument, checks if it corresponds to an aicraft description pattern
     * the string can be empty
     * @throws new IllegalArgumentException
     */

    public AircraftDescription {
        if (descriptionPattern.matcher(string).matches() || string.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
}
