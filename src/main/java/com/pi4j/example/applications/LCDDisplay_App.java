package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.LCDDisplay;

/**
 * Example Application of using the a LCD Display
 */
public class LCDDisplay_App implements Application {
    @Override
    public void execute(Context pi4j) {
        LCDDisplay lcd = new LCDDisplay(pi4j, 2, 16);
        System.out.println("Here we go.. let's have some fun with that LCD Display!");

        // Turn on the backlight makes the display appear turned on
        lcd.setDisplayBacklight(true);

        // Write text to the lines separate
        lcd.displayText("Hello");
        lcd.displayText("   World!");

        // Wait a little to have some time to read it
        sleep(3000);

        // Clear the display to start next parts
        lcd.clearDisplay();

        // To write some text there are different methods. The simplest one is this one which automatically inserts
        // linebreaks if needed.
        lcd.displayText("Boohoo that's so simple to use!");

        // Delay again
        sleep(3000);

        // Of course it is also possible to write a single line
        lcd.displayText("hard to usE!", 2);
        sleep(3000);

        // Turn off the backlight makes the display appear turned off
        lcd.setDisplayBacklight(false);
    }
}
