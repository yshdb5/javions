package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Record IcaoAddress : represents an Icao address.
 *
 * @param string : the string representing the Icao address.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record IcaoAddress(String string) {
    /**
     * Pattern of an ICAO address.
     */
    private static final Pattern OACI_Pattern = Pattern.compile("[0-9A-F]{6}");

    /**
     * Compact constructor : validates the string given in argument, checks if it corresponds to an ICAO pattern
     *
     * @throws IllegalArgumentException if it doesn't match with the pattern
     */
    public IcaoAddress {
        Preconditions.checkArgument(OACI_Pattern.matcher(string).matches());
    }

}
