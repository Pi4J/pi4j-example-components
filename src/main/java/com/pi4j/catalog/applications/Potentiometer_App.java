package com.pi4j.catalog.applications;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Ads1115;
import com.pi4j.catalog.components.Potentiometer;

/**
 * This example shows how to use the potentiometer component displaying the values of the hooked potentiometer
 * <P>
 * see <a href="https://pi4j.com/examples/components/potentiometer/">Description on Pi4J website</a>
 */
public class Potentiometer_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("Potentiometer test started ...");

        Ads1115 ads1115 = new Ads1115(pi4j);

        Potentiometer poti = new Potentiometer(ads1115, Ads1115.Channel.A0);

        //read current value from poti one time
        System.out.println(String.format("Current value of the poti is %.3f V.", poti.readCurrentVoltage()));

        //read current value from the poti in percent one time
        System.out.println(String.format("The potentiometer slider is currently at %.3f %%", poti.readNormalizedValue()));

        // Register event handlers to print a message when potentiometer is moved
        poti.onNormalizedValueChange((value) -> System.out.println(String.format("The potentiometer slider is currently at  %.3f  %% of its full travel.", value)));

        //start continuous reading with single shot in this mode you can connect up to 4 devices to the analog module
        ads1115.startContinuousReading(0.05, 10);

        // Wait while handling events before exiting
        System.out.println("Move the potentiometer to see it in action!");
        delay(10_000);

        //stop continuous reading
        ads1115.stopContinuousReading();

        System.out.println("Potentiometer test done");
    }
}
