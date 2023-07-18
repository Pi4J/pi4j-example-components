package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.LedStrip;

/**
 * This example shows how to use the LEDStrip component by setting the LEDs on the strip to different colors.
 * <p>
 * see <a href="https://pi4j.com/examples/components/ledstrip/">Description on Pi4J website</a>
 */
public class LedStrip_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("LED strip app started ...");

        // Initialize the strip
        Duration ms50  = Duration.ofMillis(50);
        Duration ms500 = Duration.ofMillis(500);
        Duration sec1  = Duration.ofSeconds(1);

        int leds = 10;  //
        final LedStrip ledStrip = new LedStrip(pi4j, leds);

        delay(sec1);

        System.out.println("LED strip shines purple");
        ledStrip.setStripColor(LedStrip.LedPixelColor.PURPLE);
        ledStrip.render(sec1);

        System.out.println("turn strip off");
        ledStrip.allOff();
        ledStrip.render(sec1);

        System.out.println("toggle between GREEN and RED");
        ledStrip.alternate(LedStrip.LedPixelColor.GREEN, LedStrip.LedPixelColor.RED, ms500, 3);

        System.out.println("setting the LEDs to blue and the first one to purple");
        ledStrip.setStripColor(LedStrip.LedPixelColor.BLUE);
        ledStrip.setPixelColor(0, LedStrip.LedPixelColor.PURPLE);
        ledStrip.render(ms500);

        System.out.println("Start a kind of animation");
        for(int i = 0; i < leds -1; i++){
            ledStrip.setPixelColor(i, LedStrip.LedPixelColor.BLUE);
            ledStrip.setPixelColor(i+1, LedStrip.LedPixelColor.PURPLE);
            ledStrip.render(ms50);
        }

        for(int i = leds -1; i > 0; i--){
            ledStrip.setPixelColor(i, LedStrip.LedPixelColor.BLUE);
            ledStrip.setPixelColor(i - 1, LedStrip.LedPixelColor.PURPLE);
            ledStrip.render(ms50);
        }
        delay(ms500);

        System.out.println("setting the brightness to full and show the first LED as white");
        ledStrip.allOff();
        ledStrip.setMaxBrightness(1);
        ledStrip.setPixelColor(0, LedStrip.LedPixelColor.WHITE);
        ledStrip.render(Duration.ofSeconds(2));

        //finishing and closing
        ledStrip.reset();

        System.out.println("LED strip demo finished.");
    }
}