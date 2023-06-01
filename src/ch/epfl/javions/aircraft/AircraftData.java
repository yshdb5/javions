package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * Record AircraftData : collects fix data of an aircraft.
 *
 * @param registration           the aircraft registration.
 * @param typeDesignator         the aircraft type designator.
 * @param model                  the aircraft model.
 * @param description            the aircraft description.
 * @param wakeTurbulenceCategory the wake turbulence category of the aircraft.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
                           AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     * Throws an exception if one of the arguments is Null using requireNonNull
     *
     * @throws NullPointerException  if one of the parameters are null.
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
