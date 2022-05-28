package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.Servo;
import com.pi4j.example.components.helpers.PIN;

public class Servo_App implements Application {
    @Override
    public void execute(Context pi4j) {

        //testing component has only a valid range in 45 degrees
        int maxDegrees = 45;
        //initialising component
        Servo servo = new Servo(pi4j, PIN.PWM18, 500, 1500, maxDegrees);

        logInfo("setting it to the lowest position");
        servo.setMin();
        delay(1000);

        logInfo("setting it to the center position");
        servo.setCenter();
        delay(1000);

        logInfo("setting it to the highest position");
        servo.setMax();
        delay(1000);

        logInfo("counting up from zero");
        for (int i = 0; i < maxDegrees; i++) {
            servo.setPositionDegrees(i);
            delay(500);
        }

        logInfo("closing the connection, shutting down the pwm");
        servo.close();
    }
}
