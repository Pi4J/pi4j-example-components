package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Ads1115;
import com.pi4j.catalog.components.JoystickAnalog;
import com.pi4j.catalog.components.base.PIN;

/**
 * This example shows how to use the analog joystick component
 * <P>
 * see <a href="https://pi4j.com/examples/components/joystickanalog/">Description on Pi4J website</a>
 */
public class JoystickAnalog_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("Joystick demo started ...");

        Ads1115 ads1115 = new Ads1115(pi4j);

        //joystick with normalized axis from -1 to 1
        JoystickAnalog joystick = new JoystickAnalog(ads1115, Ads1115.Channel.A0, Ads1115.Channel.A1, PIN.D26, true);

        //register event handlers
        joystick.onMove((xPos, yPos) -> System.out.printf("Current position of joystick is: %.2f, %.2f%n", xPos, yPos),
                        () -> System.out.println("Joystick in home position"));

        joystick.onDown      (() -> System.out.println("Pressing the button"));
        joystick.onUp        (() -> System.out.println("Stopped pressing."));
        joystick.whilePressed(() -> System.out.println("Button is still pressed."), Duration.ofMillis(500));

        //start continuous reading after all ADC channels are configured
        ads1115.startContinuousReading(0.1);

        System.out.println("Move the joystick to see it in action!");

        //wait while handling events before exiting
        delay(Duration.ofSeconds(30));

        //stop continuous reading
        joystick.reset();

        System.out.println("Joystick demo done");
    }
}
