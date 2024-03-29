package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.base.PIN;
import com.pi4j.catalog.components.SimpleLed;


/**
 * This example shows how to use the simple LED component by creating a flashing light by repeatedly toggling the LED on and off.
 * <p>
 * see <a href="https://pi4j.com/examples/components/simpleled/">Description on Pi4J website</a>
 */
public class SimpleLed_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("Simple LED demo started ...");

        // Create a new SimpleLED component
        SimpleLed led = new SimpleLed(pi4j, PIN.D26);

        // Turn on the LED to have a defined state
        System.out.println("Turn on LED.");
        led.on();
        delay(Duration.ofSeconds(1));

        // Make a flashing light by toggling the LED every second
        for (int i = 0; i < 10; i++) {
            System.out.println("Current LED state is :" + led.toggle() +".");
            delay(Duration.ofSeconds(1));
        }

        // That's it so reset all and quit
        led.reset();

        System.out.println("Simple LED demo finished.");
    }
}



