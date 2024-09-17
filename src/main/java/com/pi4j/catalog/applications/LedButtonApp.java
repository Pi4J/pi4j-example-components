package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import com.pi4j.catalog.components.base.PIN;
import com.pi4j.catalog.components.LedButton;

/**
 * This example shows how to use the LEDButton component by registering actions for the interaction with the button, while simultaneously toggling the LED
 * <p>
 * see <a href="https://pi4j.com/examples/components/ledbutton/">Description on Pi4J website</a>
 */
public class LedButtonApp {

    public static void main(String[] args) {
        final Context pi4j = Pi4J.newAutoContext();

        System.out.println("LED button demo started ...");

        // Initialize the button component
        final LedButton ledButton = new LedButton(pi4j, PIN.D26, false, PIN.D5);

        // Make a flashing light by toggling the LED
        for (int i = 0; i < 4; i++) {
            ledButton.toggleLed();
            delay(Duration.ofMillis(500));
        }

        // Register event handlers to turn LED on when pressed (onDown) and off when depressed (onUp)
        ledButton.onDown(() -> ledButton.ledOn());
        ledButton.onUp  (() -> ledButton.ledOff());

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        delay(Duration.ofSeconds(15));

        // Unregister all event handlers to exit this application in a clean way
        ledButton.reset();

        System.out.println("LED button demo finished.");
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