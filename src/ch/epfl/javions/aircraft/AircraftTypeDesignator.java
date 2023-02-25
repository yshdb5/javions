package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftTypeDesignator (String string)
{
    private static Pattern typePattern = Pattern.compile("[A-Z0-9]{2,4}");

    public AircraftTypeDesignator
    {
        Preconditions.checkArgument(typePattern.matcher(string).matches() || string.isEmpty());
    }
}
