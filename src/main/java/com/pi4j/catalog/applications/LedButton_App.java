package com.pi4j.catalog.applications;

import com.pi4j.context.Context;
import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.helpers.PIN;
import com.pi4j.catalog.components.LedButton;

/**
 * This example shows how to use the LEDButton component by registering actions for the interaction with the button, while simultaneously toggling the LED
 */
public class LedButton_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("LED button app started ...");

        // Initialize the button component
        final LedButton ledButton = new LedButton(pi4j, PIN.D26, Boolean.FALSE, PIN.PWM19);

        // Turn on the LED to have a defined state
        ledButton.ledSetStateOn();
        //see the LED for a Second
        delay(1000);

        // Register event handlers to print a message when pressed (onDown) and depressed (onUp)
        ledButton.btnOnDown(() -> System.out.println("Pressing the Button"));
        ledButton.btnOnUp(()   -> System.out.println("Stopped pressing."));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");

        // Make a flashing light by toggling the LED every second
        // in the meantime, the Button can still be pressed, as we only freeze the main thread
        for (int i = 0; i < 15; i++) {
            System.out.println(ledButton.ledToggleState());
            delay(1000);
        }

        // Unregister all event handlers to exit this application in a clean way
        ledButton.btnDeRegisterAll();
        ledButton.ledSetStateOff();

        System.out.println("LED button app done.");
    }
}