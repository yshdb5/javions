package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.PowerWindow;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest
{
    @Test
    void PowerWindowThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");

        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, 0));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, 1 << 17));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, -1));
        assertDoesNotThrow(() -> new PowerWindow(stream, 1<<16));
    }

    @Test
    void SizeWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 12);
        int actual = window.size();
        int expected = 12;
        assertEquals(expected, actual);
    }

    @Test
    void PositionWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 100);
        window.advance();
        window.advanceBy(5);
        window.advanceBy(0);
        long actual = window.position();
        long expected = 6;
        assertEquals(expected, actual);
    }

    @Test
    void IsFullWorksOnKnownValues1() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        boolean actual = window.isFull();
        boolean expected = true;
        assertEquals(expected, actual);
    }

    @Test
    void IsFullWorksOnKnownValues2() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1202);
        boolean actual = window.isFull();
        boolean expected = false;
        assertEquals(expected, actual);
    }

    @Test
    void IsFullWorksOnKnownValues3() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        window.advance();
        boolean actual = window.isFull();
        boolean expected = false;
        assertEquals(expected, actual);
    }

    @Test
    void GetIWorksOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        window.advanceBy(2);
        int actual = window.get(1);
        int expected = 745;
        assertEquals(expected, actual);

        window.advanceBy(48);
        actual = window.get(0);
        expected = 160;
        assertEquals(expected, actual);
    }

    @Test
    void GetIWorksOnKnownValues2() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 10);
        int actual = window.get(0);
        int expected = 73;
        assertEquals(expected, actual);
        window.advanceBy(2);
        actual = window.get(0);
        expected = 65;
        assertEquals(expected, actual);
        actual = window.get(9);
        expected = 1657;
        assertEquals(expected, actual);
    }

    @Test
    void GetIThrowsIndexOutOfBoundsException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(1201));
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(1202));
        assertDoesNotThrow(() -> window.get(0));
    }

    /*
    @Test // Ne marche qu'avec un batchsize de 8 dans powerWindow
    void TheTabsExchangesWorkOnKnownValues() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 5);

        int actual = window.get(0);
        int expected = 73;
        assertEquals(expected, actual);

        actual = window.get(4);
        expected = 98;
        assertEquals(expected, actual);

        window.advanceBy(5);
        actual = window.get(0);
        expected = 4226;
        assertEquals(expected, actual);

        actual = window.get(4);
        expected = 23825;
        assertEquals(expected, actual);

        window.advanceBy(5);
        actual = window.get(0);
        expected = 10730;
        assertEquals(expected, actual);

        actual = window.get(4);
        expected = 394;
        assertEquals(expected, actual);

        window.advanceBy(40);
        actual = window.get(0);
        expected = 160;
        assertEquals(expected, actual);

        actual = (int) window.position();
        expected = 50;
        assertEquals(expected, actual);

        actual = (int) window.position();
        expected = 50;
        assertEquals(expected, actual);
    }

     */

    @Test
    void AdvanceByThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);

        assertThrows(IllegalArgumentException.class, () -> window.advanceBy(-1));
        assertDoesNotThrow(() -> window.advanceBy(0));
    }

    @Test
    void checkGet() throws IOException
    {
        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, StandardCharsets.UTF_8);
        InputStream file=new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(file, 2);
        powerWindow.advanceBy(8);
        assertEquals(23825, powerWindow.get(1));
        file.close();
    }

    @Test
    public void checkAnotherGet() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2 , StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);

        PowerWindow powerWindow = new PowerWindow(stream, 5);
        powerWindow.advanceBy(7);
        assertEquals(1657,powerWindow.get(4));

    }


}