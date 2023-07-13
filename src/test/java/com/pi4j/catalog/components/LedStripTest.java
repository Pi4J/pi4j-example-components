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
        strip = new LedStrip(pi4j, pixels);
        spi = strip.mock();
    }


    @Test
    public void testSetPixelColor() {
        //when
        strip.allOff();
        strip.setPixelColor(1, LedStrip.LedPixelColor.YELLOW);

        //then
        assertEquals(LedStrip.LedPixelColor.YELLOW, strip.getPixelColor(1));
    }

    @Test
    public void testSetStripColor() {
        //when
        strip.allOff();
        strip.setStripColor(LedStrip.LedPixelColor.YELLOW);

        //then
        assertEquals(LedStrip.LedPixelColor.YELLOW, strip.getPixelColor(5));
    }

    @Test
    public void testGetNumLEDS() {
        assertEquals(pixels, strip.getNumPixels());
    }

    @Test
    public void testGetPixelChannels() {
        //when
        int red = LedStrip.LedPixelColor.RED;
        int blue = LedStrip.LedPixelColor.BLUE;
        int green = LedStrip.LedPixelColor.GREEN;
        int white = LedStrip.LedPixelColor.WHITE;

        //then
        assertEquals(LedStrip.LedPixelColor.getRedComponent(red), 255);
        assertEquals(LedStrip.LedPixelColor.getGreenComponent(red), 0);
        assertEquals(LedStrip.LedPixelColor.getBlueComponent(red), 0);

        assertEquals(LedStrip.LedPixelColor.getRedComponent(green), 0);
        assertEquals(LedStrip.LedPixelColor.getGreenComponent(green), 255);
        assertEquals(LedStrip.LedPixelColor.getBlueComponent(green), 0);

        assertEquals(LedStrip.LedPixelColor.getRedComponent(blue), 0);
        assertEquals(LedStrip.LedPixelColor.getGreenComponent(blue), 0);
        assertEquals(LedStrip.LedPixelColor.getBlueComponent(blue), 255);

        assertEquals(255, LedStrip.LedPixelColor.getRedComponent(white));
        assertEquals(255, LedStrip.LedPixelColor.getGreenComponent(white));
        assertEquals(255, LedStrip.LedPixelColor.getBlueComponent(white));
    }
}
