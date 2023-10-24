package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.base.PIN;
import com.pi4j.catalog.components.SimpleButton;

/**
 * This example shows how to use the SimpleButton component by registering events for different interactions with the button
 * <P>
 * see <a href="https://pi4j.com/examples/components/simplebutton/">Description on Pi4J website</a>
 */
public class SimpleButton_App implements Application {

    @Override
    public void execute(Context pi4j) {
        System.out.println("Simple button demo started ...");

        // Initialize the button component
        final var button = new SimpleButton(pi4j, PIN.D26, Boolean.FALSE);

        // Register event handlers to print a message when pressed (onDown) and depressed (onUp)
        button.onDown      (() -> System.out.println("Button pressed"));
        button.whilePressed(() -> System.out.println("Still pressing"), Duration.ofSeconds(1));
        button.onUp        (() -> System.out.println("Stopped pressing"));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        delay(Duration.ofSeconds(15));

        // Unregister all event handlers to exit this application in a clean way
        button.reset();

        /*
        if you want to deRegister only a single function, you can do so like this:
        button.onUp(null);
        */

        System.out.println("Simple button demo finished.");
    }
}