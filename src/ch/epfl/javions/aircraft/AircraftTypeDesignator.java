package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftTypeDesignator (String string)
{
    private static Pattern typePattern = Pattern.compile("[A-Z0-9]{2,4}");

    public AircraftTypeDesignator
    {
        if (typePattern.matcher(string).matches() || string.isEmpty())
        {
            throw new IllegalArgumentException();
        }
    }
}
