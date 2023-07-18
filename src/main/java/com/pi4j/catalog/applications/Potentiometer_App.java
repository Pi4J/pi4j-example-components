package com.pi4j.catalog.applications;

import java.time.Duration;

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
        System.out.println("Potentiometer demo started ...");

        // a potentiometer needs an ADC
        Ads1115 ads1115 = new Ads1115(pi4j);

        Potentiometer poti = new Potentiometer(ads1115, Ads1115.Channel.A0);

        //read current value from poti one time
        System.out.printf("P0 raw value is %.2f V%n", poti.readCurrentVoltage());

        //read current value from the poti in percent one time
        System.out.printf("P0 normalized value is %.2f %%%n", poti.readNormalizedValue());

        // Register event handlers to print a message when potentiometer is moved
        poti.onNormalizedValueChange((value) -> System.out.printf("P0 slider is at %.2f %%%n", value));

        //you can attach a second potentiometer to another channel, if you like:
//        Potentiometer potiWithCenterPosition = new Potentiometer(ads1115, Ads1115.Channel.A1, Potentiometer.Range.MINUS_ONE_TO_ONE);
//        potiWithCenterPosition.onNormalizedValueChange((value) -> System.out.printf("P1 slider is at %.2f %%", value)));

        //you have to start continuous reading on ADC (because you can use up to 4 channels and all of them need to be fully configured before starting to read the values)
        ads1115.startContinuousReading(0.1);

        System.out.println("Move the potentiometer to see it in action!");
        // Wait while handling events before exiting
        delay(Duration.ofSeconds(15));

        ads1115.stopContinuousReading();

        System.out.println("No new values should be reported");
        delay(Duration.ofSeconds(5));

        ads1115.reset();
        System.out.println("Potentiometer demo finished");
    }
}
