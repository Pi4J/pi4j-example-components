package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.LEDStrip;

/**
 * This example shows how to use the LEDStrip component by setting the LEDs on the strip to different colors
 */
public class LEDStrip_App implements Application {
    @Override
    public void execute(Context pi4j) {
        // Initialize the RGB
        int pixels = 10;
        final var ledstrip = new LEDStrip(pi4j, pixels, 0.5);

        //set them all off, so nothing is shining
        System.out.println("Starting with setting all leds off");
        ledstrip.allOff();

        System.out.println("setting the leds to RED");
        ledstrip.setStripColor(LEDStrip.PixelColor.RED);
        ledstrip.render();
        delay(3000);

        System.out.println("setting the leds to Light Blue");
        ledstrip.setStripColor(LEDStrip.PixelColor.LIGHT_BLUE);
        ledstrip.render();
        delay(3000);

        System.out.println("setting the first led to Purple");
        ledstrip.setPixelColor(0, LEDStrip.PixelColor.PURPLE);
        ledstrip.render();
        delay(3000);

        System.out.println("setting the brightness to full and just show the first led as White");
        ledstrip.allOff();
        ledstrip.setBrightness(1);
        ledstrip.setPixelColor(0, LEDStrip.PixelColor.WHITE);
        ledstrip.render();
        delay(3000);

        //finishing and closing
        ledstrip.close();
        System.out.println("closing the app");
        System.out.println("Color "+ ledstrip.getPixelColor(0));
    }
}