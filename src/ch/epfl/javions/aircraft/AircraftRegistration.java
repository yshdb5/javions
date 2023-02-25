package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftRegistration(String string)
{
    private static Pattern registrationPattern = Pattern.compile("[A-Z0-9 .?/_+-]+");

    public AircraftRegistration
    {
        Preconditions.checkArgument(registrationPattern.matcher(string).matches());
    }
}
