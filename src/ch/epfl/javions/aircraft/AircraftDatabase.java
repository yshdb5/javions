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

        try (ZipFile zipFile = new ZipFile(dataBaseName);
             InputStream stream = zipFile.getInputStream(zipFile.getEntry(String.valueOf(address)));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader))
        {
             String line = "";
             while ((line = bufferedReader.readLine()) != null)
                 System.out.println(line);
        }
        return new AircraftData(null, null, null,null,null);
    }
}
