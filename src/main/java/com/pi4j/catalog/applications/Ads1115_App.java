package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Ads1115;

/**
 * This example shows how to use the analog-to-digital converter (ADC, A/D or A-to-D) 'ADS1115' that converts an
 * analog signal, like the current 'position' of a potentiometer into a digital signal.
 * <p>
 * see <a href="https://pi4j.com/examples/components/ads1115/">Description on Pi4J website</a>
 */
public class Ads1115_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("ADS1115 demo started ...");

        Ads1115 adc = new Ads1115(pi4j);

        System.out.println("read all channels in single mode");
        singleRead(adc);

        System.out.println("read all channels in continuous mode");
        continuousRead(adc);

        //cleanup
        adc.reset();

        System.out.println("ADS1115 demo finished");
    }

    private void singleRead(Ads1115 adc) {
        System.out.println("Single read started ...");
        //read analog value from all four channels
        double aIn0 = adc.readValue(Ads1115.Channel.A0);
        double aIn1 = adc.readValue(Ads1115.Channel.A1);
        double aIn2 = adc.readValue(Ads1115.Channel.A2);
        double aIn3 = adc.readValue(Ads1115.Channel.A3);
        System.out.printf("Voltages: a0=%.3f V, a1=%.3f V, a2=%.3f V, a3=%.3f V%n", aIn0, aIn1, aIn2, aIn3);

        System.out.println("Single read done.");
    }

    private void continuousRead(Ads1115 adc) {
        System.out.println("Continuous read started ...");

        // Register event handlers to print a message on value change
        adc.onValueChange(Ads1115.Channel.A0, (value) -> System.out.printf("Value channel 0 : %.2f V%n", value));
        adc.onValueChange(Ads1115.Channel.A1, (value) -> System.out.printf("Value channel 1 : %.2f V%n", value));
        adc.onValueChange(Ads1115.Channel.A2, (value) -> System.out.printf("Value channel 2 : %.2f V%n", value));
        adc.onValueChange(Ads1115.Channel.A3, (value) -> System.out.printf("Value channel 3 : %.2f V%n", value));

        adc.startContinuousReading(0.1);

        // continue reading for 30 seconds
        delay(Duration.ofSeconds(30));

        adc.stopContinuousReading();

        System.out.println("Continuous read done.");
    }

}
