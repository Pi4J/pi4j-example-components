package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.helpers.PIN;
import com.pi4j.example.components.SimpleLED;


/**
 * This example shows how to use the simple LED component by creating a flashing light by repeatedly toggling the LED on and off.
 */
public class SimpleLED_App implements Application {
    @Override
    public void execute(Context pi4j) {
        logInfo("Simple LED app started ...");

        // Create a new SimpleLED component
        SimpleLED led = new SimpleLED(pi4j, PIN.D26);

        // Turn on the LED to have a defined state
        logInfo("Turn on LED.");
        led.setStateOn();
        delay(1000);

        // Make a flashing light by toggling the LED every second
        for (int i = 0; i < 10; i++) {
            logInfo("Current LED state is " + led.toggleState() +".");
            delay(1000);
        }

        // That's all so turn off the relay and quit
        led.setStateOff();
        logInfo("Turn off LED.");
        delay(2000);

        logInfo("Simple LED app done.");
    }
}



