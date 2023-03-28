package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * final class AircraftDatabase : represents the database of aircraft.
 *
 * @author Yshai  (356356)
 * @author Gabriel Taieb (360560)
 */


public final class AircraftDatabase {
    private final String fileName;

    /**
     * the constructor : checks that its argument is not null and stores it in an attribute of the class
     *
     * @param fileName
     * @throws NullPointerException if the file is null
     */
    public AircraftDatabase(String fileName) {
        Objects.requireNonNull(fileName);

        this.fileName = fileName;
    }

    /**
     * @param address the ICAO address
     * @return the data of the aircraft whose ICAO address is the one given
     * @throws IOException in case of input/output error
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        String csvAddress = address.string().substring(4) + ".csv";

        String[] splitData;

        try (ZipFile zipFile = new ZipFile(fileName);
             InputStream stream = zipFile.getInputStream(zipFile.getEntry(csvAddress));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                if (line.compareTo(address.string()) < 0) {
                    continue;
                }
                if (line.startsWith(address.string())) {
                    splitData = line.split(",", -1);

                    return new AircraftData(new AircraftRegistration(splitData[1]), new AircraftTypeDesignator(splitData[2]), splitData[3],
                            new AircraftDescription(splitData[4]), WakeTurbulenceCategory.of(splitData[5]));
                }
                if (line.compareTo(address.string()) > 0) {
                    return null;
                }
            }
        }
        return null;
    }
}
