package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Ads1115;

/**
 * <P>
 * see <a href="https://pi4j.com/examples/components/ads1115/">Description on Pi4J website</a>
 */
public class Ads1115_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("ADS1115 demo started ...");

        System.out.println("read all channels in single mode");
        singleRead(pi4j);

        // System.out.println("read all channels in continuous mode");
        //continuousReadAllChannels(pi4j);

        // System.out.println("read one channel in fast continuous mode");
        //continuousReadChannel0(pi4j);

        System.out.println("ADS1115 test done");
    }

    private void singleRead(Context pi4j) {
        System.out.println("Single read started ...");
        Ads1115 adc = new Ads1115(pi4j);

        //read analog value from all four channels
        for (int i = 0; i < 30; i++) {
            double aIn0 = adc.readValue(Ads1115.Channel.A0);
            double aIn1 = adc.readValue(Ads1115.Channel.A1);
            double aIn2 = adc.readValue(Ads1115.Channel.A2);
            double aIn3 = adc.readValue(Ads1115.Channel.A3);
            System.out.printf("[" + i + "] Voltages: a0=%.3f V, a1=%.3 V, a2=%.3f V, a3=%.3f V", aIn0, aIn1, aIn2, aIn3);
            //wait for next read
            delay(Duration.ofSeconds(1));
        }

        pi4j.shutdown();

        //end test
        System.out.println("Single read done.");
    }

    private void continuousReadAllChannels(Context pi4j) {
        //start test
        System.out.println("Continuous read started ...");

        Ads1115 ads1115 = new Ads1115(pi4j);

        // Register event handlers to print a message on value change
        ads1115.onValueChange(Ads1115.Channel.A0, (value) -> System.out.printf("The actual value from channel 0 is: %.2f V%n", value));
        ads1115.onValueChange(Ads1115.Channel.A1, (value) -> System.out.printf("The actual value from channel 1 is: %.2f V%n", value));
        ads1115.onValueChange(Ads1115.Channel.A2, (value) -> System.out.printf("The actual value from channel 2 is: %.2f V%n", value));
        ads1115.onValueChange(Ads1115.Channel.A3, (value) -> System.out.printf("The actual value from channel 3 is: %.2f V%n", value));

        //start continuous measuring
        ads1115.startContinuousReading(0.1);

        // Wait while handling events before exiting
        delay(Duration.ofSeconds(30));

        //stop continuous measuring
        ads1115.stopContinuousReading();

        //deregister all handlers
        ads1115.reset();

        pi4j.shutdown();

        //end test
        System.out.println("Continuous read done.");
    }

    private void continuousReadChannel0(Context pi4j) {
        //start test
        System.out.println("Continuous read of Channel 0 started ...");

        Ads1115 ads1115 = new Ads1115(pi4j);

        // Register event handler to print a message on value change
        ads1115.onValueChange(Ads1115.Channel.A0, (value) -> {
            System.out.println("Value changed, now it's : %.2f V");
        });

        ads1115.startContinuousReading(0.1);

        // Wait while handling events before exiting
        delay(Duration.ofSeconds(30));

        //deregister all handlers
        ads1115.reset();

        pi4j.shutdown();

        //end test
        System.out.println("Continuous read of Channel 0  done.");
    }
}
