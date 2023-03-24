package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * record IcaoAddress : represents an Icao adress
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public record IcaoAddress(String string) {
    private static Pattern OACI_Pattern = Pattern.compile("[0-9A-F]{6}");

    /**
     * compact constuctor : valids the string given in argument, checks if it corresponds to an OAIC pattern
     *
     * @throws IllegalArgumentException
     */
    public IcaoAddress {
        Preconditions.checkArgument(OACI_Pattern.matcher(string).matches());
    }

}
