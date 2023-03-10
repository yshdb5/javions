package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator
{
    private PowerWindow powerWindow;

    public AdsbDemodulator(InputStream samplesStream) throws IOException
    {
        powerWindow = new PowerWindow(samplesStream, 1200);
    }

    public RawMessage nextMessage() throws IOException
    {
        long timeStamp = 0;


        return new RawMessage(timeStamp, null);
    }
}
