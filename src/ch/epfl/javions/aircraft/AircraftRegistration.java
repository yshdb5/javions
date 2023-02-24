package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftRegistration(String string)
{
    private static Pattern registrationPattern = Pattern.compile("[A-Z0-9 .?/_+-]+");

    public AircraftRegistration
    {
        if (registrationPattern.matcher(string).matches())
        {
            throw new IllegalArgumentException();
        }
    }
}
