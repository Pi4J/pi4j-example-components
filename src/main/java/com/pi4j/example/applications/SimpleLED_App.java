package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.PIN;
import com.pi4j.example.components.SimpleLED;


/**
 * This example shows how to use the simple LED component by creating a flashing light by repeatedly toggling the LED on and off.
 */
public class SimpleLED_App implements Application {
    @Override
    public void execute(Context pi4j) {
        // Create a new SimpleLED component
        SimpleLED led = new SimpleLED(pi4j, PIN.D26);

        // Turn on the LED to have a defined state
        led.setStateOn();
        delay(1000);

        // Make a flashing light by toggling the LED every second
        for (int i = 0; i < 10; i++) {
            System.out.println(led.toggleState());
            delay(1000);
        }

        // That's all so turn off the relay and quit
        led.setStateOff();
        System.out.println("off");
        delay(2000);
    }
}



