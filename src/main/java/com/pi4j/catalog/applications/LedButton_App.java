package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.LedButton;
import com.pi4j.catalog.components.base.PIN;

/**
 * This example shows how to use the LEDButton component by registering actions for the interaction with the button, while simultaneously toggling the LED
 * <P>
 * see <a href="https://pi4j.com/examples/components/ledbutton/">Description on Pi4J website</a>
 */
public class LedButton_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("LED button demo started ...");

        // Initialize the button component
        final LedButton ledButton = new LedButton(pi4j, PIN.D26, false, PIN.PWM19);

        // Turn on the LED to have a defined state
        ledButton.ledOn();
        //see the LED for a Second
        delay(Duration.ofSeconds(1));

        // Register event handlers to print a message when pressed (onDown) and depressed (onUp)
        ledButton.onDown(() -> System.out.println("Pressing the Button"));
        ledButton.onUp  (() -> System.out.println("Stopped pressing."));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");

        // Make a flashing light by toggling the LED every second
        // in the meantime, the Button can still be pressed, as we only freeze the main thread
        for (int i = 0; i < 15; i++) {
            System.out.println(ledButton.toggleLed());
            delay(Duration.ofSeconds(1));
        }

        // Unregister all event handlers to exit this application in a clean way
        ledButton.reset();

        System.out.println("LED button demo done.");
    }
}