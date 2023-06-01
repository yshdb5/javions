package ch.epfl.javions.aircraft;

/**
 * Enumerate type WakeTurbulenceCategory.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 *
 * Represents the category of turbulence of an aircraft.
 */
public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**
     * Converts the textual values of the database to enumerate type.
     *
     * @param s the string that give us the information for the type of turbulence
     * @return the category of turbulence that corresponds to the string given
     */
    public static WakeTurbulenceCategory of(String s) {
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }

}
