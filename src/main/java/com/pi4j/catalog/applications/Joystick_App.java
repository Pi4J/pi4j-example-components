package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Joystick;
import com.pi4j.catalog.components.base.PIN;

/**
 * This example shows how to use the Joystick by registering actions for each position of the joystick
 * <P>
 * see <a href="https://pi4j.com/examples/components/joystick/">Description on Pi4J website</a>
 */
public class Joystick_App implements Application {
    @Override
    public void execute(Context pi4j) {

        System.out.println("Joystick app started ...");
        final var joystick = new Joystick(pi4j, PIN.D5, PIN.D6, PIN.PWM13, PIN.PWM19, PIN.D26);

        //Register event handlers to print a message when pressed (onDown) and (onUp)
        joystick.onNorth(() -> System.out.println("Start Pressing joystick button North"));
        joystick.whileNorth(() -> System.out.println("Pressing joystick button North"), Duration.ofSeconds(1));

        joystick.onWest(() -> System.out.println("Start Pressing joystick button West"));
        joystick.whileWest(() -> System.out.println("Pressing joystick button West"), Duration.ofSeconds(1));

        joystick.onSouth(() -> System.out.println("Start Pressing joystick button South"));
        joystick.whileSouth(() -> System.out.println("Pressing joystick button South"), Duration.ofSeconds(1));

        joystick.onEast(() -> System.out.println(" Start Pressing joystick button East"));
        joystick.whileEast(() -> System.out.println("Pressing joystick button East"), Duration.ofSeconds(1));

        joystick.onPushDown(() -> System.out.println("Start Pressing joystick button PUSH"));
        joystick.onPushUp(() -> System.out.println("Stop pressing joystick button PUSH"));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        delay(15_000);

        // Unregister all event handlers to exit this application in a clean way
        joystick.reset();

        System.out.println("Joystick app done.");
    }
}
