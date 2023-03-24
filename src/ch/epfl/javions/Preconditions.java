package ch.epfl.javions;

/**
 * class Preconditions.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class Preconditions {


    private Preconditions() {
    }

    /**
     * Checks if the argument given is valid
     *
     * @param shouldBeTrue the argument should be true
     * @throws IllegalArgumentException if the argument is invalid
     */

    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
