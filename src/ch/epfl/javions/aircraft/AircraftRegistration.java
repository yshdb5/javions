package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Record AircraftRegistration : represents an aircraft registration.
 *
 * @param string the string representation of the registration.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftRegistration(String string) {
    /**
     * Pattern of a valid registration.
     */
    private static final Pattern registrationPattern = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * Compact constructor : validates the string given in argument, checks if it is an Aircraft registration.
     *
     * @throws IllegalArgumentException it doesn't match with the pattern or the string is empty
     */
    public AircraftRegistration {
        Preconditions.checkArgument(registrationPattern.matcher(string).matches());
    }
}
