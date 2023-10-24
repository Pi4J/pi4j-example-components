package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.base.PIN;
import com.pi4j.catalog.components.Joystick;

/**
 * This example shows how to use the Joystick by registering actions for each position of the joystick
 * <p>
 * see <a href="https://pi4j.com/examples/components/joystick/">Description on Pi4J website</a>
 */
public class Joystick_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("Joystick demo started ...");

        final var joystick = new Joystick(pi4j, PIN.D5, PIN.D6, PIN.PWM13, PIN.PWM19, PIN.D26);

        //Register all event handlers
        joystick.onNorth(() -> System.out.println("Start NORTH"));
        joystick.whileNorth(() -> System.out.println("Still NORTH"),
                            Duration.ofSeconds(1));

        joystick.onWest(() -> System.out.println("Start WEST"));
        joystick.whileWest(() -> System.out.println("Still WEST"),
                           Duration.ofSeconds(1));

        joystick.onSouth(() -> System.out.println("Start SOUTH"));
        joystick.whileSouth(() -> System.out.println("Still SOUTH"),
                            Duration.ofSeconds(1));

        joystick.onEast(() -> System.out.println(" Start EAST"));
        joystick.whileEast(() -> System.out.println("Still EAST"),
                          Duration.ofSeconds(1));

        joystick.onPushDown(() -> System.out.println("Start PUSH"));
        joystick.onPushUp(() -> System.out.println("Still PUSHing"));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Move the joystick and push it's button to see it in action!");
        delay(Duration.ofSeconds(15));

        // cleanup
        joystick.reset();

        System.out.println("Joystick demo finished.");
    }
}
