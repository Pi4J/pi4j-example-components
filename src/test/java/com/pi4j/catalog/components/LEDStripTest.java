package com.pi4j.catalog.components;

import com.pi4j.catalog.ComponentTest;
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
        strip = new LEDStrip(pi4j, pixels, 0.5);
        spi = toMock(strip.spi);
    }


    @Test
    public void testSetPixelColor() {
        //when
        strip.allOff();
        strip.setPixelColor(1, LEDStrip.PixelColor.YELLOW);

        //then
        assertEquals(LEDStrip.PixelColor.YELLOW, strip.getPixelColor(1));
    }

    @Test
    public void testSetStripColor() {
        //when
        strip.allOff();
        strip.setStripColor(LEDStrip.PixelColor.YELLOW);

        //then
        assertEquals(LEDStrip.PixelColor.YELLOW, strip.getPixelColor(5));
    }

    @Test
    public void testGetNumLEDS() {
        assertEquals(pixels, strip.getNumPixels());
    }

    @Test
    public void testGetPixelChannels() {
        //when
        int red = LEDStrip.PixelColor.RED;
        int blue = LEDStrip.PixelColor.BLUE;
        int green = LEDStrip.PixelColor.GREEN;
        int white = LEDStrip.PixelColor.WHITE;

        //then
        assertEquals(LEDStrip.PixelColor.getRedComponent(red), 255);
        assertEquals(LEDStrip.PixelColor.getGreenComponent(red), 0);
        assertEquals(LEDStrip.PixelColor.getBlueComponent(red), 0);

        assertEquals(LEDStrip.PixelColor.getRedComponent(green), 0);
        assertEquals(LEDStrip.PixelColor.getGreenComponent(green), 255);
        assertEquals(LEDStrip.PixelColor.getBlueComponent(green), 0);

        assertEquals(LEDStrip.PixelColor.getRedComponent(blue), 0);
        assertEquals(LEDStrip.PixelColor.getGreenComponent(blue), 0);
        assertEquals(LEDStrip.PixelColor.getBlueComponent(blue), 255);

        assertEquals(255, LEDStrip.PixelColor.getRedComponent(white));
        assertEquals(255, LEDStrip.PixelColor.getGreenComponent(white));
        assertEquals(255, LEDStrip.PixelColor.getBlueComponent(white));
    }
}
