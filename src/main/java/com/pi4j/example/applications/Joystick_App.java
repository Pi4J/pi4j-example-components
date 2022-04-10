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
        joystick.buttonUpOnDown(() -> System.out.println("Pressing joystick button UP"));
        joystick.buttonUpOnUp(() -> System.out.println("Stop pressing joystick button UP"));

        joystick.buttonLeftOnDown(() -> System.out.println("Pressing joystick button LEFT"));
        joystick.buttonLeftOnUp(() -> System.out.println("Stop pressing joystick button LEFT"));

        joystick.buttonDownOnDown(() -> System.out.println("Pressing joystick button DOWN"));
        joystick.buttonDownOnUp(() -> System.out.println("Stop pressing joystick button DOWN"));

        joystick.buttonRightOnDown(() -> System.out.println("Pressing joystick button RIGHT"));
        joystick.buttonRightOnUp(() -> System.out.println("Stop pressing joystick button RIGHT"));

        joystick.buttonPushOnDown(() -> System.out.println("Pressing joystick button PUSH"));
        joystick.buttonPushOnUp(() -> System.out.println("Stop pressing joystick button PUSH"));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        sleep(15000);

        // Unregister all event handlers to exit this application in a clean way
        joystick.buttonUpOnDown(null);
        joystick.buttonUpOnUp(null);

        joystick.buttonLeftOnDown(null);
        joystick.buttonLeftOnUp(null);

        joystick.buttonDownOnDown(null);
        joystick.buttonDownOnUp(null);

        joystick.buttonRightOnDown(null);
        joystick.buttonRightOnUp(null);

        joystick.buttonPushOnDown(null);
        joystick.buttonPushOnUp(null);

        joystick.buttonUpOnDown(null);
        joystick.buttonUpOnUp(null);

    }
}
