package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase
{
    private String fileName;
    public AircraftDatabase(String fileName)
    {
        Objects.requireNonNull(fileName);

        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException
    {
        String dataBaseName = getClass().getResource(fileName).getFile();
        String csvAddress = address.string().substring(4) + ".csv";

        String [] splittedData = new String[5];

        try (ZipFile zipFile = new ZipFile(dataBaseName);
             InputStream stream = zipFile.getInputStream(zipFile.getEntry(csvAddress));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader))
        {
             String line = "";

             while ((line = bufferedReader.readLine()) != null)
             {
                 if (line.compareTo(address.string()) < 0)
                 {
                     continue;
                 }
                 if (line.startsWith(address.string());
                 {
                     splittedData = line.split(",");
                 }
                 if (line.compareTo(address.string()) > 0)
                 {
                     return null;
                 }
             }
        }


        return new AircraftData(new AircraftRegistration(splittedData[1]), new AircraftTypeDesignator(splittedData[2]), splittedData[3],
                new AircraftDescription(splittedData[4]), WakeTurbulenceCategory.of(splittedData[5]));
    }
}
