package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * record AircraftRegistration : represents an aircraft registration
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftRegistration(String string) {
    private static final Pattern registrationPattern = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * compact constructor : valids the string given in argument, checks if it is an Aircraft registration
     *
     * @throws IllegalArgumentException
     */
    public AircraftRegistration {
        Preconditions.checkArgument(registrationPattern.matcher(string).matches());
    }
}
