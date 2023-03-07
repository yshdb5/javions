package ch.epfl.demodulation;

import ch.epfl.javions.demodulation.PowerWindow;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

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
        int actual = window.get(2);
        int expected = 745;
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

    @Test
    void AdvanceWorksOnKnownValues() throws IOException
    {}

    @Test
    void AdvanceByWorksOnKnownValues() throws IOException
    {}

    @Test
    void AdvanceByThrowsIllegalArgumentException() throws IOException
    {
        FileInputStream stream = new FileInputStream("resources/samples.bin");
        PowerWindow window = new PowerWindow(stream, 1201);

        assertThrows(IllegalArgumentException.class, () -> window.advanceBy(-1));
        assertDoesNotThrow(() -> window.advanceBy(0));
    }


}