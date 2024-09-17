package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import com.pi4j.catalog.components.LcdDisplay;

/**
 * This example shows how to use the LCDDisplay component by writing different things to the display
 * <p>
 * see <a href="https://pi4j.com/examples/components/lcddisplay/">Description on Pi4J website</a>
 */
public class LcdDisplayApp {
    public static void main(String[] args) {
        final Context pi4j = Pi4J.newAutoContext();
        System.out.println("LCD demo started");

        //Create a Component, with amount of ROWS and COLUMNS of the device
        //LcdDisplay lcd = new LcdDisplay(pi4j); //2x16 is default
        LcdDisplay lcd = new LcdDisplay(pi4j, 4, 20);

        // Write text to specific position
        lcd.displayLineOfText("Hello" , 0);
        lcd.displayLineOfText("World!", 1, 3);

        // Wait a little to have some time to read it
        delay(Duration.ofSeconds(3));

        lcd.clearDisplay();

        lcd.centerTextInLine("Hi", 0);

        delay(Duration.ofSeconds(1));

        // To write some text there are different methods. The simplest one is this one which automatically inserts
        // linebreaks if needed.
        lcd.displayText("Boohoo that's so simple to use!");
        delay(Duration.ofSeconds(3));

        // Of course, it is also possible to use linebreaks
        lcd.displayText("Some big text \nwith a new line\nonly displayed on 4 row LCD");
        delay(Duration.ofSeconds(4));

        // Long text are cut to the bone
        lcd.displayText("Some big text with no new lines, just to test how many lines will get filled");
        delay(Duration.ofSeconds(4));

        // Clear the display to start next parts
        lcd.clearDisplay();

        // Let's try to draw a house.
        // To keep this method short and clean we create the characters in a separate method below.
        createCharacters(lcd);

        // Now all characters are ready. Just draw them on the right place by moving the cursor and writing the
        // created characters to specific positions
        lcd.writeCharacter('\1', 0, 1);
        lcd.writeCharacter('\2', 0, 2);
        lcd.writeCharacter('\3', 1, 1);
        lcd.writeCharacter('\4', 1, 2);

        delay(Duration.ofSeconds(3));

        // we've built a rolling home
        for (int i = 0; i < 5; i++) {
            lcd.scrollRight();
            delay(Duration.ofSeconds(1));
        }

        for (int i = 0; i < 5; i++) {
            lcd.scrollLeft();
            delay(Duration.ofSeconds(1));
        }

        lcd.reset();
        System.out.println("LCD demo finished");
    }

    public static void createCharacters(LcdDisplay lcd) {
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

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param duration Time to sleep
     */
    private static void delay(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
