package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import com.pi4j.example.components.helpers.PixelColor;
import com.pi4j.plugin.mock.provider.spi.MockSpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LEDStripTest extends ComponentTest {

    private MockSpi spi;
    private LEDStrip strip;
    private int pixels;

    @BeforeEach
    public void setUp() {
        this.pixels = 10;
        strip = new LEDStrip(pi4j, pixels, 127);
        spi = toMock(strip.spi);
    }


    @Test
    public void testSetPixelColor() {
        //when
        strip.allOff();
        strip.setPixelColor(1, PixelColor.YELLOW);

        //then
        assertEquals(PixelColor.YELLOW, strip.getPixelColor(1));
    }

    @Test
    public void testSetStripColor() {
        //when
        strip.allOff();
        strip.setStripColor(PixelColor.YELLOW);

        //then
        assertEquals(PixelColor.YELLOW, strip.getPixelColor(5));
    }

    @Test
    public void testGetNumLEDS() {
        assertEquals(pixels, strip.getNumPixels());
    }

    @Test
    public void testGetPixelChannels() {
        //when
        int red = PixelColor.RED;
        int blue = PixelColor.BLUE;
        int green = PixelColor.GREEN;
        int white = PixelColor.WHITE;

        //then
        assertEquals(PixelColor.getRedComponent(red), 255);
        assertEquals(PixelColor.getGreenComponent(red), 0);
        assertEquals(PixelColor.getBlueComponent(red), 0);

        assertEquals(PixelColor.getRedComponent(green), 0);
        assertEquals(PixelColor.getGreenComponent(green), 255);
        assertEquals(PixelColor.getBlueComponent(green), 0);

        assertEquals(PixelColor.getRedComponent(blue), 0);
        assertEquals(PixelColor.getGreenComponent(blue), 0);
        assertEquals(PixelColor.getBlueComponent(blue), 255);

        assertEquals(255, PixelColor.getRedComponent(white));
        assertEquals(255, PixelColor.getGreenComponent(white));
        assertEquals(255, PixelColor.getBlueComponent(white));
    }
}
