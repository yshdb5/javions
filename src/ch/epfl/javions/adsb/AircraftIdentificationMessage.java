package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * record AircraftIdentificationMessage : represents an ADS-B message of identification and category
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */


public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {
    /**
     * @param timeStampNs the time stamp of the message, in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param category    the category of aircraft of the shipper
     * @param callSign    the sender's call sign
     * @throws NullPointerException if icaoAddress or callSign are nul
     * @throws NullPointerException if timeStampNs is strictly less than 0.
     */
    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * redefinition of timeStamps
     *
     * @return
     */
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    /**
     * redefinition of icaoAddress
     *
     * @return
     */
    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    /**
     * @param rawMessage
     * @return the identification message corresponding to the given raw message,
     * or null if at least one of the characters of the code it contains is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();

        int categoryByte = Bits.extractUInt(rawMessage.payload(), 48, 3);

        int[] tab = getTab(rawMessage.payload());

        StringBuilder callstring = new StringBuilder();

        for (int i : tab) {
            if (i >= 1 && i <= 26) {
                callstring.append((char) (i + 64));
            } else if ((i >= 48 && i <= 57) || (i == 32)) {
                callstring.append((char) i);
            } else {
                return null;
            }
        }

        while (callstring.toString().endsWith(" ")) {
            callstring = new StringBuilder(callstring.substring(0, (callstring.length() - 1)));
        }

        long timeStamps = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int category = Byte.toUnsignedInt((byte) (((14 - typeCode) << 4) | categoryByte));

        CallSign callSign = new CallSign(callstring.toString());

        return new AircraftIdentificationMessage(timeStamps, icaoAddress, category, callSign);
    }

    private static int[] getTab(long payload) {
        int[] tab = new int[8];
        int bitStart = 42;
        for (int i = 0; i < 8; i++) {
            tab[i] = Bits.extractUInt(payload, bitStart, 6);
            bitStart -= 6;
        }
        return tab;
    }
}
