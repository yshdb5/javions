package ch.epfl.javions;/*
 /**
 * class Units : define the different units used.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */

public final class Units
{
    private Units() {}

    /**
     constants that define the IS prefixes
     */
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    /**
     * class Angle : angle units
     *
     * @author Yshai  (356356)
     * @author Gabriel Taieb (360560)
     */
    public static class Angle
    {
        private Angle (){}

        /**
         series of attributes (constants) defining the angle units
         the basic unit is the radiant (value set to 1), the other units are defined in relation to this unit
         */
        public static final double RADIAN = 1;
        public static final double TURN = 2*Math.PI*RADIAN;
        public static final double DEGREE = TURN/360;
        public static final double T32= TURN/Math.scalb(1, 32);
    }

    /**
     * class Length : lenght units
     *
     * @author Yshai  (356356)
     * @author Gabriel Taieb (360560)
     */
    public static class Length
    {
        private Length() {}

        /**
         series of attributes (constants) defining the units of length
         the base unit is the meter (of value 1), the other units are defined in relation to this unit
         */

        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54*CENTIMETER;
        public static final double FOOT = 12*INCH;
        public static final double NAUTICAL_MILE = 1852*METER;
    }

    public static class Time
    {
        private Time() {}

        /**
         series of attributes (constants) defining the units of time
         the base unit is the second (of value 1), the other units are defined in relation to this unit
         */

        public static final double SECOND = 1;
        public static final double MINUTE = 60*SECOND;
        public static final double HOUR = 60*MINUTE;
    }

    public static class Speed
    {
        private Speed(){}

        /**
         attributes (constants) defining the units of speed
         defined by using the classes Lenght and Time
         */
        public static final double KNOT = Length.NAUTICAL_MILE/Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER/Time.HOUR;
    }


    /**
     *converts the given value, expressed in the unit FromUnit, into the unit toUnit
     *
     * @param value
     *      the value
     * @param fromUnit
     *      the starting unit
     * @param toUnit
     *      the input unit
     * @return the value in the toUnit unit
     */
    public static double convert(double value, double fromUnit, double toUnit)
    {
        return value * (fromUnit / toUnit);
    }

    /**
     *same thing as to convert when toUnit is the base unit.
     *
     * @param value
     *      the value
     * @param fromUnit
     *      the starting unit
     * @return the value in the toUnit unit
     */

    public static double convertFrom(double value, double fromUnit)
    {
        return (value * fromUnit);
    }

    /**
     *same thing as to convert when fromUnit is the base unit.
     *
     * @param value
     *      the value

     * @return the value in the toUnit unit
     */
    public static double convertTo(double value, double toUnit)
    {
        return (value / toUnit);
    }
}
