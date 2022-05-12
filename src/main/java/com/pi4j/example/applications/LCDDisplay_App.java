package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.LCDDisplay;

/**
 * Example Application of using the LCD Display
 */
public class LCDDisplay_App implements Application {
    @Override
    public void execute(Context pi4j) {
        //Create a Component, with amount of ROWS and COLUMNS of the Device
        LCDDisplay lcd = new LCDDisplay(pi4j, 4, 20);
        System.out.println("Here we go.. let's have some fun with that LCD Display!");

        // Turn on the backlight makes the display appear turned on
        lcd.setDisplayBacklight(true);

        // Write text to the lines separate
        lcd.displayText("Hello", 1);
        lcd.displayText("   World!", 2);
        lcd.displayText("Line 3", 3);
        lcd.displayText("   Line 4", 4);

        // Wait a little to have some time to read it
        delay(3000);

        // Clear the display to start next parts
        lcd.clearDisplay();

        // To write some text there are different methods. The simplest one is this one which automatically inserts
        // linebreaks if needed.
        lcd.displayText("Boohoo that's so simple to use!");

        // Delay again
        delay(3000);

        // Of course it is also possible to write with newLine Chars
        lcd.displayText("Some big text \nwith some new Lines \n just for testing");
        delay(3000);

        // Of course it is also possible to write long text
        lcd.displayText("Some big text with no new lines, just to test how many lines will get filled");
        delay(3000);

        lcd.displayText("Small text with \nnew line char");
        delay(3000);

        // Clear the display to start next parts
        lcd.clearDisplay();

        // Turn off the backlight makes the display appear turned off
        lcd.setDisplayBacklight(false);
    }

    public void createCharacters(LCDDisplay lcd) {
        // Create upper left part of the house
        lcd.createCharacter(1, new byte[]{
                0b00000,
                0b00000,
                0b00000,
                0b00001,
                0b00011,
                0b00111,
                0b01111,
                0b11111
        });

        // Create upper right part of the house
        lcd.createCharacter(2, new byte[]{
                0b00000,
                0b00000,
                0b00010,
                0b10010,
                0b11010,
                0b11110,
                0b11110,
                0b11111
        });

        // Create lower left part of the house
        lcd.createCharacter(3, new byte[]{
                0b11111,
                0b11111,
                0b11111,
                0b11111,
                0b10001,
                0b10001,
                0b10001,
                0b10001
        });

        // Create lower right part of the house
        lcd.createCharacter(4, new byte[]{
                0b11111,
                0b11111,
                0b11111,
                0b10001,
                0b10001,
                0b10001,
                0b11111,
                0b11111
        });
    }
}
