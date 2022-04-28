package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.PIN;
import com.pi4j.example.components.Joystick;

public class Joystick_App implements Application {
    @Override
    public void execute(Context pi4j) {

        //Initzalize the joystick component
        final var joystick = new Joystick(pi4j, PIN.D5, PIN.D6, PIN.PWM13, PIN.PWM19, PIN.D26);

        //Register event handlers to print a message when pressed (onDown) and (onUp)
        joystick.onNorth(() -> System.out.println("Start Pressing joystick button North"));
        joystick.whileNorth(1000, () -> System.out.println("Pressing joystick button North"));

        joystick.onWest(() -> System.out.println("Start Pressing joystick button West"));
        joystick.whileWest(1000, () -> System.out.println("Pressing joystick button West"));

        joystick.onSouth(() -> System.out.println("Start Pressing joystick button South"));
        joystick.whileSouth(1000, () -> System.out.println("Pressing joystick button South"));

        joystick.onEast(() -> System.out.println(" Start Pressing joystick button East"));
        joystick.whileEast(1000, () -> System.out.println("Pressing joystick button East"));

        joystick.onPushDown(() -> System.out.println("Start Pressing joystick button PUSH"));
        joystick.onPushUp(() -> System.out.println("Stop pressing joystick button PUSH"));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        delay(15000);

        // Unregister all event handlers to exit this application in a clean way
        joystick.deRegisterAll();

    }
}
