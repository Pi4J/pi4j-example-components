package com.pi4j.catalog.applications;

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

        Ads1115 ads1115 = new Ads1115(pi4j, 0x01, Ads1115.GAIN.GAIN_4_096V, Ads1115.ADDRESS.GND, 4);

        //joystick with normalized axis from 0 to 1
        JoystickAnalog joystick = new JoystickAnalog(pi4j, ads1115, 0, 1, 3.3, true, PIN.D26);

        //joystick with normalized axis from -1 to 1
        //JoystickAnalog joystick = new JoystickAnalog(pi4j, ads1115, 0, 1, 3.3, false, PIN.D26);

        //register event handlers
        joystick.xOnMove((value) -> System.out.println("Current value of joystick x axis is: " + String.format("%.3f", value)));
        joystick.yOnMove((value) -> System.out.println("Current value of joystick y axis is: " + String.format("%.3f", value)));

        joystick.pushOnDown(() -> System.out.println("Pressing the Button"));
        joystick.pushOnUp(() -> System.out.println("Stopped pressing."));
        joystick.pushWhilePressed(() -> System.out.println("Button is still pressed."), 1000);

        joystick.calibrateJoystick();

        //start continuous reading with single shot in this mode you can connect up to 4 devices to the analog module
        joystick.start(0.05, 10);

        //wait while handling events before exiting
        System.out.println("Move the joystick to see it in action!");

        delay(30_000);

        //stop continuous reading
        joystick.stop();

        delay(1000
        );

        //deregister all event handlers
        joystick.deregisterAll();

        System.out.println("Joystick test done");
    }
}
