package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * record AircraftData : collects fix data of an aircraft
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
                           AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     * throws an exception if one of the arguments is Null using requireNonNull
     *
     * @param registration           the aircraft registration
     * @param typeDesignator         the aircraft type Designator
     * @param model                  the aircraft model
     * @param description            the aircraft description
     * @param wakeTurbulenceCategory the turbulence category of the aircraft
     * @throws NullPointerException checks that the parameters are not null
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
