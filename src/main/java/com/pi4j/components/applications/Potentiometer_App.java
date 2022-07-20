package com.pi4j.components.applications;

import com.pi4j.context.Context;
import com.pi4j.components.Application;
import com.pi4j.components.components.ADS1115;
import com.pi4j.components.components.Potentiometer;

import static java.lang.Thread.sleep;

/**
 * This example shows how to use the potentiometer component displaying the values of the hooked potentiometer
 */
public class Potentiometer_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("Potentiometer test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x01, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        Potentiometer poti = new Potentiometer(ads1115, 0, 3.3);

        //read current value from poti one time
        System.out.println("Current value of the poti is " + String.format("%.3f", poti.singleShotGetVoltage()) + " voltage.");

        //read current value from the poti in percent one time
        System.out.println("The potentiometer slider is currently at " + String.format("%.3f", poti.singleShotGetNormalizedValue()) + " % of its full travel.");

        // Register event handlers to print a message when potentiometer is moved
        poti.setConsumerSlowReadChan((value) -> {
            System.out.println("The potentiometer slider is currently at " + String.format("%.3f", value) + " % of its full travel.");
        });

        //start continuous reading with single shot in this mode you can connect up to 4 devices to the analog module
        poti.startSlowContinuousReading(0.05, 10);

        // Wait while handling events before exiting
        System.out.println("Move the potentiometer to see it in action!");
        delay(30_000);

        //stop continuous reading
        poti.stopSlowContinuousReading();

        System.out.println("Potentiometer test done");
    }
}
