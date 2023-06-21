package com.pi4j.catalog.applications;

import com.pi4j.context.Context;
import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.LcdDisplay;

/**
 * This example shows how to use the LCDDisplay component by writing different things to the display
 * <P>
 * see <a href="https://pi4j.com/examples/components/lcddisplay/">Description on Pi4J website</a>
 */
public class LcdDisplay_App implements Application {
    @Override
    public void execute(Context pi4j) {
        //Create a Component, with amount of ROWS and COLUMNS of the Device
        LcdDisplay lcd = new LcdDisplay(pi4j, 4, 20);
        System.out.println("Here we go.. let's have some fun with that LCD Display!");

        // Turn on the backlight makes the display appear turned on
        lcd.setDisplayBacklight(true);

        // Write text to the lines separate
        lcd.displayText("Hello", 1);
        lcd.displayText("   World!", 2);
        //A display with just 2 lines would fail by here
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

        // Of course, it is also possible to write with newLine Chars
        lcd.displayText("Some big text \nwith some new Lines \n just for testing");
        delay(3000);

        // Of course, it is also possible to write long text
        lcd.displayText("Some big text with no new lines, just to test how many lines will get filled");
        delay(3000);

        // Clear the display to start next parts
        lcd.clearDisplay();

        // Let's try to draw a house. To keep this method short and clean we create the characters in a separate
        // method below.
        createCharacters(lcd);

        // Now all characters are ready. Just draw them on the right place by moving the cursor and writing the
        // created characters to specific positions
        lcd.writeCharacter('\1', 1, 1);
        lcd.writeCharacter('\2', 2, 1);
        lcd.writeCharacter('\3', 1, 2);
        lcd.writeCharacter('\4', 2, 2);
        delay(3000);

        // Turn off the backlight makes the display appear turned off
        lcd.setDisplayBacklight(false);
    }

    public void createCharacters(LcdDisplay lcd) {
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
