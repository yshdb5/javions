package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * record IcaoAddress : represents an Icao address
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public record IcaoAddress(String string) {
    private static final Pattern OACI_Pattern = Pattern.compile("[0-9A-F]{6}");

    /**
     * compact constructor : valids the string given in argument, checks if it corresponds to an ICAO pattern
     *
     * @throws IllegalArgumentException
     */
    public IcaoAddress {
        Preconditions.checkArgument(OACI_Pattern.matcher(string).matches());
    }

}
