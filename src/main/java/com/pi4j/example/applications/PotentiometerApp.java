package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;
import com.pi4j.example.components.Potentiometer;

import static java.lang.Thread.sleep;

public class PotentiometerApp implements Application {

    @Override
    public void execute(Context pi4j) {
        logInfo("Potentiometer test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j);

        Potentiometer poti = new Potentiometer(ads1115, ADS1115.MUX.AIN0_GND, 3.3);

        //read current value from poti one time
        logInfo("Current value of the poti is " + poti.getVoltage() + " voltage.");

        //read current value from the poti in percent one time
        logInfo("The potentiometer slider is currently at " + poti.getPercent() + " % of its full travel.");

        // Register event handlers to print a message when poti is moved
        poti.setRunnable(() -> {
            logInfo("The current voltage drop is currently " + poti.getActualValue() + " volts");
        });

        //start continious reading with single shot in this mode you can connect up to 4 devices to the analog module
        poti.startSlowContiniousReading(0.1, 1);

        // Wait while handling events before exiting
        logInfo("Move the potentiometer to see it in action!");
        delay(30_000);

        //stop continious reading
        poti.stopSlowContiniousReading();


        logInfo("Potentiometer test done");
    }
}
