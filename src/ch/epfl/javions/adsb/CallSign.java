package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * record CallSign : represents the call sign od an aircraft
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record CallSign(String string) {
    private static final Pattern callSignPattern = Pattern.compile("[A-Z0-9 ]{0,8}");

    /**
     * compact constructor : valids the string given in argument, checks if it corresponds to a call sign pattern
     * the string can be empty
     *
     * @throws IllegalArgumentException
     */
    public CallSign {
        Preconditions.checkArgument(callSignPattern.matcher(string).matches() || string.isEmpty());
    }
}
