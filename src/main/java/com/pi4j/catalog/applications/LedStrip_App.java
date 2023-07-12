package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.LedStrip;

/**
 * This example shows how to use the LEDStrip component by setting the LEDs on the strip to different colors
 * <p>
 * see <a href="https://pi4j.com/examples/components/ledstrip/">Description on Pi4J website</a>
 */
public class LedStrip_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("LED strip app started ...");
        // Initialize the strip
        int pixels = 10;
        final LedStrip ledStrip = new LedStrip(pi4j, pixels);

        System.out.println("setting the LEDs to GREEN");
        ledStrip.setStripColor(LedStrip.PixelColor.GREEN);
        ledStrip.render();

        System.out.println("setting the LEDs to RED");
        ledStrip.setStripColor(LedStrip.PixelColor.RED);
        ledStrip.render();
        delay(Duration.ofSeconds(1));

        System.out.println("setting the LEDs to Blue");
        ledStrip.setStripColor(LedStrip.PixelColor.BLUE);
        System.out.println("setting the first led to Purple");
        ledStrip.setPixelColor(0, LedStrip.PixelColor.PURPLE);
        ledStrip.render();

        for(int i = 0; i<9; i++){
            ledStrip.setPixelColor(i, LedStrip.PixelColor.BLUE);
            ledStrip.setPixelColor(i+1, LedStrip.PixelColor.PURPLE);
            ledStrip.render();
            //delay(Duration.ofMillis(300));
        }

        System.out.println("setting the brightness to full and just show the first led as White");
        ledStrip.allOff();
        ledStrip.setMaxBrightness(1);
        ledStrip.setPixelColor(0, LedStrip.PixelColor.WHITE);
        ledStrip.render();
        delay(Duration.ofSeconds(3));



        //finishing and closing
        ledStrip.reset();
        System.out.println("closing the app");
        System.out.println("Color "+ ledStrip.getPixelColor(0));

        System.out.println("LED strip app done.");
    }
}