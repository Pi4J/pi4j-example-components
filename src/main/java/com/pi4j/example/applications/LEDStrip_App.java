package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.LEDStrip;
import com.pi4j.example.components.helpers.PixelColor;

public class LEDStrip_App implements Application {
    @Override
    public void execute(Context pi4j) {
        // Initialize the RGB
        int pixels = 10;
        final var ledstrip = new LEDStrip(pi4j, pixels, 127);

        //set them all off, so nothing is shining
        logInfo("Starting with setting all leds off");
        ledstrip.allOff();

        logInfo("setting the leds to RED");
        ledstrip.setStripColor(PixelColor.RED);
        ledstrip.render();
        delay(3000);

        logInfo("setting the leds to Light Blue");
        ledstrip.setStripColor(PixelColor.LIGHT_BLUE);
        ledstrip.render();
        delay(3000);

        logInfo("setting the first led to Purple");
        ledstrip.setPixelColor(0, PixelColor.PURPLE);
        ledstrip.render();
        delay(3000);

        logInfo("setting the brightness to full and just show the first led as White");
        ledstrip.allOff();
        ledstrip.setBrightness(255);
        ledstrip.setPixelColor(0, PixelColor.WHITE);
        ledstrip.render();
        delay(3000);

        //finishing and closing
        ledstrip.close();
        logInfo("closing the app");
        logInfo("Color "+ ledstrip.getPixelColor(0));
    }
}