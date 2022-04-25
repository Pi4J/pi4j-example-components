package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.Joystick;

public class Joystick_App implements Application {
    @Override
    public void execute(Context pi4j) {

        //Initzalize the joystick component
        final var joystick = new Joystick(pi4j, 5,6,13,19,26);

        //Register event handlers to print a message when pressed (onDown) and (onUp)
        joystick.buttonNorthOnDown(() -> System.out.println("Start Pressing joystick button North"));
        joystick.buttonNorthwhilePressed(1000, () -> System.out.println("Pressing joystick button North"));

        joystick.buttonWestOnDown(() -> System.out.println("Start Pressing joystick button West"));
        joystick.buttonWestwhilePressed(1000, () -> System.out.println("Pressing joystick button West"));

        joystick.buttonSouthOnDown(() -> System.out.println("Start Pressing joystick button South"));
        joystick.buttonSouthwhilePressed(1000, () -> System.out.println("Pressing joystick button South"));

        joystick.buttonEastOnDown(() -> System.out.println(" Start Pressing joystick button East"));
        joystick.buttonEastwhilePressed(1000, () -> System.out.println("Pressing joystick button East"));

        joystick.buttonPushOnDown(() -> System.out.println("Start Pressing joystick button PUSH"));
        joystick.buttonPushOnUp(() -> System.out.println("Stop pressing joystick button PUSH"));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        sleep(15000);

        // Unregister all event handlers to exit this application in a clean way
        joystick.deRegisterAll();

    }
}
