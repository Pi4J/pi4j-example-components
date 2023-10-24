package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.plugin.mock.provider.spi.MockSpi;

import com.pi4j.catalog.ComponentTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LedStripTest extends ComponentTest {

    private LedStrip strip;
    private int pixels;

    @BeforeEach
    public void setUp() {
        pixels = 10;
        strip = new LedStrip(pi4j, pixels);
    }


    @Test
    public void testSetPixelColor() {
        //given
        strip.setMaxBrightness(1.0);

        //when
        strip.allOff();
        strip.setPixelColor(1, LedStrip.LedColor.YELLOW);

        //then
        assertEquals(LedStrip.LedColor.YELLOW, strip.getPixelColor(1));
    }

    @Test
    public void testSetStripColor() {
        //given
        strip.setMaxBrightness(1.0);

        //when
        strip.allOff();
        strip.setStripColor(LedStrip.LedColor.YELLOW);

        //then
        for(int i= 0; i<pixels; i++){
            assertEquals(LedStrip.LedColor.YELLOW, strip.getPixelColor(i));
        }
    }

    @Test
    public void testGetNumLEDS() {
        assertEquals(pixels, strip.getNumPixels());
    }

    @Test
    public void testGetPixelChannels() {
        //when
        int red = LedStrip.LedColor.RED;
        int blue = LedStrip.LedColor.BLUE;
        int green = LedStrip.LedColor.GREEN;
        int white = LedStrip.LedColor.WHITE;

        //then
        assertEquals(LedStrip.LedColor.getRedComponent(red), 255);
        assertEquals(LedStrip.LedColor.getGreenComponent(red), 0);
        assertEquals(LedStrip.LedColor.getBlueComponent(red), 0);

        assertEquals(LedStrip.LedColor.getRedComponent(green), 0);
        assertEquals(LedStrip.LedColor.getGreenComponent(green), 255);
        assertEquals(LedStrip.LedColor.getBlueComponent(green), 0);

        assertEquals(LedStrip.LedColor.getRedComponent(blue), 0);
        assertEquals(LedStrip.LedColor.getGreenComponent(blue), 0);
        assertEquals(LedStrip.LedColor.getBlueComponent(blue), 255);

        assertEquals(255, LedStrip.LedColor.getRedComponent(white));
        assertEquals(255, LedStrip.LedColor.getGreenComponent(white));
        assertEquals(255, LedStrip.LedColor.getBlueComponent(white));
    }
}
