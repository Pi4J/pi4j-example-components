package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;
import com.pi4j.example.components.JoystickAnalog;
import com.pi4j.example.components.helpers.PIN;

public class JoystickAnalog_App implements Application {
    @Override
    public void execute(Context pi4j) {
        logInfo("Joystick test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x01, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        //joystick with normalized axis from 0 to 1
        JoystickAnalog joystick = new JoystickAnalog(pi4j, ads1115, 0, 1, 3.3, true, PIN.D26);
        //joystick with normalized axis form -1 to 1
        //JoystickAnalog joystick = new JoystickAnalog(pi4j, ads1115, 0, 1, 3.3, false, PIN.D26);

        //register event handlers
        joystick.xOnMove(() -> {
            logInfo("Current value of joystick x axis is: " + String.format("%.3f", joystick.getXValue()));
        });
        joystick.yOnMove(() -> {
            logInfo("Current value of joystick y axis is: " + String.format("%.3f", joystick.getYValue()));
        });

        joystick.pushOnDown(() -> logInfo("Pressing the Button"));
        joystick.pushOnUp(() -> logInfo("Stopped pressing."));
        joystick.pushWhilePressed(() -> logInfo("Button is still pressed."), 1000);

        joystick.start(0.1,10);

        delay(1000);

        System.out.println("Joystick x value before calibration is: " +  String.format("%.3f", joystick.getXValue()));
        System.out.println("Joystick y value before calibration is: " +  String.format("%.3f", joystick.getYValue()));

        delay(1000);

        joystick.stop();

        delay(1000);

        joystick.calibrateJoystick();

        joystick.start(0.1, 10);

        delay(1000);

        System.out.println("Joystick x value after calibration is: " +  String.format("%.3f", joystick.getXValue()));
        System.out.println("Joystick y value after calibration is: " +  String.format("%.3f", joystick.getYValue()));

        delay(1000);

        joystick.stop();

        delay(1000);

        //start continuous reading with single shot in this mode you can connect up to 4 devices to the analog module
        joystick.start(0.1, 10);

        //wait while handling events before exiting
        logInfo("Move the joystick to see it in action!");

        delay(50_000);

        //stop continuous reading
        joystick.stop();

        delay(1000
        );

        //deregister all event handlers
        joystick.deregisterAll();

        logInfo("Joystick test done");
    }
}
