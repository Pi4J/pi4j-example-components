package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Ads1115;
import com.pi4j.catalog.components.JoystickAnalog;
import com.pi4j.catalog.components.base.PIN;

/**
 * This example shows how to use the analog joystick component by registering different interactions for the positions of the joystick
 * <P>
 * see <a href="https://pi4j.com/examples/components/joystickanalog/">Description on Pi4J website</a>
 */
public class JoystickAnalog_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("Joystick test started ...");

        Ads1115 ads1115 = new Ads1115(pi4j);

        //joystick with normalized axis from -1 to 1
        JoystickAnalog joystick = new JoystickAnalog(ads1115, Ads1115.Channel.A0, Ads1115.Channel.A1, PIN.D26);

        //register event handlers
        joystick.onHorizontalChange((value) -> System.out.println("Current value of joystick x axis is: " + String.format("%.2f", value)));
        joystick.onVerticalChange((value)   -> System.out.println("Current value of joystick y axis is: " + String.format("%.2f", value)));

        joystick.onDown      (() -> System.out.println("Pressing the button"));
        joystick.onUp        (() -> System.out.println("Stopped pressing."));
        joystick.whilePressed(() -> System.out.println("Button is still pressed."), Duration.ofSeconds(1));

        //start continuous reading
        joystick.start(0.05, 10);

        System.out.println("Move the joystick to see it in action!");

        //wait while handling events before exiting
        delay(30_000);

        //stop continuous reading
        joystick.stop();

        delay(1000);

        //deregister all event handlers
        joystick.reset();

        System.out.println("Joystick test done");
    }
}
