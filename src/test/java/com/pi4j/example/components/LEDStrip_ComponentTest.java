package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import com.pi4j.example.components.helpers.PixelColor;
import com.pi4j.plugin.mock.provider.spi.MockSpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LEDStrip_ComponentTest extends ComponentTest {

    private MockSpi spi;
    private LEDStrip strip;
    private int pixels;

    @BeforeEach
    public void setUp(){
        this.pixels = 10;
        strip = new LEDStrip(pi4j, pixels, 127);
        spi = toMock(strip.spi);
    }

    @Test
    public void testGetContext(){
        assertEquals(pi4j, strip.getContext());
    }

    @Test
    public void testSetPixelColor(){
        //when
        strip.allOff();
        strip.setPixelColor(1, PixelColor.YELLOW);

        //then
        assertEquals(PixelColor.YELLOW, strip.getPixelColor(1));
    }

    @Test
    public void testSetStripColor(){
        //when
        strip.allOff();
        strip.setStripColor(PixelColor.YELLOW);

        //then
        assertEquals(PixelColor.YELLOW, strip.getPixelColor(5));
    }

    @Test
    public void testGetNumLEDS(){
        assertEquals(pixels, strip.getNumPixels());
    }
}
