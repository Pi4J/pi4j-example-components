package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.LEDStrip;
import com.pi4j.example.components.helpers.PIN;
import com.pi4j.example.components.helpers.PixelColor;

public class LEDStrip_App implements Application {
    @Override
    public void execute(Context pi4j) {
        // Initialize the RGB
        int pixels = 8;
        final var ledstrip = new LEDStrip(pi4j, PIN.D26, pixels, 127);

        //set them all off, so nothing is shining
        logger.info("Starting with setting all leds off");
        ledstrip.allOff();

        for(int i = 0; i < 5; i++){

            //increment red value
            logger.info("setting the leds to a shade of red");
            int red = 0;
            for (int j = 0; j < pixels; j++) {
                ledstrip.setPixelColor(j, red);
                red += 255 / pixels;
            }
            //show it on the strip
            ledstrip.render();
            delay(2000);

            //increment green value
            logger.info("setting the leds to a shade of green");
            int green = 0;
            for (int j = 0; j < pixels; j++) {
                ledstrip.setPixelColor(j, green);
                green += 255 / pixels;
            }
            //show it on the strip
            ledstrip.render();
            delay(2000);

            //increment blue value
            logger.info("setting the leds to a shade of blue");
            int blue = 0;
            for (int j = 0; j < pixels; j++) {
                ledstrip.setPixelColor(j, blue);
                blue += 255 / pixels;
            }
            //show it on the strip
            ledstrip.render();
            delay(2000);
        }

        logger.info("setting the leds to purple");
        ledstrip.setStripColor(PixelColor.PURPLE);
        ledstrip.render();
        delay(3000);

        //finishing and closing
        ledstrip.close();
        logger.info("closing the app");
    }
}