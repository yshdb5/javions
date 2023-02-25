package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record IcaoAdress(String string)
{
    private static Pattern OACI_Pattern = Pattern.compile("[0-9A-F]{6}");

    public IcaoAdress
    {
        Preconditions.checkArgument(OACI_Pattern.matcher(string).matches());
    }

}
