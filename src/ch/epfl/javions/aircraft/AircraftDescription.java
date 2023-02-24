package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftDescription (String string)
{
    private static Pattern descriptionPattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
    public AircraftDescription {
        if (descriptionPattern.matcher(string).matches() || string.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
}
