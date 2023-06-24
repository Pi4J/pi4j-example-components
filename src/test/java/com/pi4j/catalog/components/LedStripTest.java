package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.plugin.mock.provider.spi.MockSpi;

import com.pi4j.catalog.ComponentTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LedStripTest extends ComponentTest {

    private MockSpi spi;
    private LedStrip strip;
    private int pixels;

    @BeforeEach
    public void setUp() {
        this.pixels = 10;
        strip = new LedStrip(pi4j, pixels, 0.5);
        spi = strip.mock();
    }


    @Test
    public void testSetPixelColor() {
        //when
        strip.allOff();
        strip.setPixelColor(1, LedStrip.PixelColor.YELLOW);

        //then
        assertEquals(LedStrip.PixelColor.YELLOW, strip.getPixelColor(1));
    }

    @Test
    public void testSetStripColor() {
        //when
        strip.allOff();
        strip.setStripColor(LedStrip.PixelColor.YELLOW);

        //then
        assertEquals(LedStrip.PixelColor.YELLOW, strip.getPixelColor(5));
    }

    @Test
    public void testGetNumLEDS() {
        assertEquals(pixels, strip.getNumPixels());
    }

    @Test
    public void testGetPixelChannels() {
        //when
        int red = LedStrip.PixelColor.RED;
        int blue = LedStrip.PixelColor.BLUE;
        int green = LedStrip.PixelColor.GREEN;
        int white = LedStrip.PixelColor.WHITE;

        //then
        assertEquals(LedStrip.PixelColor.getRedComponent(red), 255);
        assertEquals(LedStrip.PixelColor.getGreenComponent(red), 0);
        assertEquals(LedStrip.PixelColor.getBlueComponent(red), 0);

        assertEquals(LedStrip.PixelColor.getRedComponent(green), 0);
        assertEquals(LedStrip.PixelColor.getGreenComponent(green), 255);
        assertEquals(LedStrip.PixelColor.getBlueComponent(green), 0);

        assertEquals(LedStrip.PixelColor.getRedComponent(blue), 0);
        assertEquals(LedStrip.PixelColor.getGreenComponent(blue), 0);
        assertEquals(LedStrip.PixelColor.getBlueComponent(blue), 255);

        assertEquals(255, LedStrip.PixelColor.getRedComponent(white));
        assertEquals(255, LedStrip.PixelColor.getGreenComponent(white));
        assertEquals(255, LedStrip.PixelColor.getBlueComponent(white));
    }
}
