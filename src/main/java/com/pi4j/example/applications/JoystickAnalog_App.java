package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;
import com.pi4j.example.components.JoystickAnalog;

public class JoystickAnalog_App implements Application {
    @Override
    public void execute(Context pi4j) {
        logInfo("Joystick test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x01, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        JoystickAnalog joystick = new JoystickAnalog(pi4j, ads1115);

        //register event handlers
        joystick.setXRunnable(() -> {
            logInfo("Current value of joystick x axis is: " + String.format("%.3f", joystick.getXValue()));
        });
        joystick.setYRunnable(() -> {
            logInfo("Current value of joystick y axis is: " + String.format("%.3f", joystick.getYValue()));
        });
        joystick.setPushOnDown(() -> logInfo("Pressing the Button"));
        joystick.setPushOnUp(() -> logInfo("Stopped pressing."));
        joystick.setPushWhilePressed(() -> logInfo("Button is still pressed."), 1000);

        //start continious reading with single shot in this mode you can connect up to 4 devices to the analog module
        joystick.start(0.1, 10);

        //wait while handling events before exiting
        logInfo("Move the joystick to see it in action!");
        delay(30_000);

        //stop continious reading
        joystick.stop();

        //deregister all event handlers
        joystick.deregisterAll();

        logInfo("Joystick test done");
    }
}
