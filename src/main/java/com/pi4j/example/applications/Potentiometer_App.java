package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;
import com.pi4j.example.components.Potentiometer;

import static java.lang.Thread.sleep;

public class Potentiometer_App implements Application {

    @Override
    public void execute(Context pi4j) {
        logInfo("Potentiometer test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x01, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND,4);

        Potentiometer poti = new Potentiometer(ads1115, 0, 3.3);

        //read current value from poti one time
        logInfo("Current value of the poti is " + String.format("%.3f",poti.singleShotGetVoltage()) + " voltage.");

        //read current value from the poti in percent one time
        logInfo("The potentiometer slider is currently at " + String.format("%.3f", poti.singleShotGetNormalizedValue()) + " % of its full travel.");

        // Register event handlers to print a message when potentiometer is moved
        poti.setConsumerSlowReadChan((value) -> {
            logInfo("The current voltage drop is currently " + String.format("%.3f", value) + " volts");
        });

        //start continious reading with single shot in this mode you can connect up to 4 devices to the analog module
        poti.startSlowContiniousReading(0.05, 10);

        // Wait while handling events before exiting
        logInfo("Move the potentiometer to see it in action!");
        delay(30_000);

        //stop continious reading
        poti.stopSlowContiniousReading();

        logInfo("Potentiometer test done");
    }
}
