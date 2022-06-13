package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;

public class ADS1115_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("ADS1115 test started ...");
        //System.out.println("read all channels in single mode");
        //singleRead(pi4j);

        System.out.println("read all channels in continuous mode");
        continuousSlowRead(pi4j);

        //System.out.println("read one channel in fast continuous mode");
        //continuousFastRead(pi4j);

        System.out.println("ADS1115 test done");
    }

    private void singleRead(Context pi4j) {
        //start test
        System.out.println("Single read started ...");
        System.out.println("Create ADS1115 object");
        ADS1115 adc = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        //read analog value from all four channels
        for (int i = 0; i < 30; i++) {
            double aIn0 = adc.singleShotAIn0();
            double aIn1 = adc.singleShotAIn1();
            double aIn2 = adc.singleShotAIn2();
            double aIn3 = adc.singleShotAIn3();
            System.out.println("[" + i + "] Voltages: a0=" + String.format("%.3f", aIn0) + " V, a1=" + String.format("%.3f", aIn1) + " V, a2=" + String.format("%.3f", aIn2) + " V, a3=" + String.format("%.3f", aIn3) + " V");
            //wait for next read
            delay(1000);
        }

        pi4j.shutdown();

        //end test
        System.out.println("Single read done.");
    }

    private void continuousSlowRead(Context pi4j) {
        //start test
        System.out.println("Continuous slow read test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        // Register event handlers to print a message on value change
        ads1115.setConsumerSlowReadChannel0((value) -> System.out.println("The actual value from channel 0 is: " + String.format("%.3f", value) + "voltage."));
        ads1115.setConsumerSlowReadChannel1((value) -> System.out.println("The actual value from channel 1 is: " + String.format("%.3f", value) + "voltage."));
        ads1115.setConsumerSlowReadChannel2((value) -> System.out.println("The actual value from channel 2 is: " + String.format("%.3f", value) + "voltage."));
        ads1115.setConsumerSlowReadChannel3((value) -> System.out.println("The actual value from channel 3 is: " + String.format("%.3f", value) + "voltage."));

        //start continuous measuring
        ads1115.startSlowContinuousReadingAllChannels(0.1, 10);

        // Wait while handling events before exiting
        delay(30000);

        //stop continuous measuring
        ads1115.stopSlowReadContinuousReadingAllChannels();

        //deregister all handlers
        ads1115.deregisterAll();

        pi4j.shutdown();

        //end test
        System.out.println("Continuous slow read test done.");
    }

    private void continuousFastRead(Context pi4j) {
        //start test
        System.out.println("Continuous fast read test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        // Register event handlers to print a message on value change
        ads1115.setConsumerFastRead((value) -> {
            System.out.println("The actual value from fast read channel is: " + String.format("%.3f", value) + "voltage.");
        });

        for (int i = 0; i < 4; i++) {
            //start continuous measuring
            ads1115.startFastContinuousReading(i, 0.1, 10);

            // Wait while handling events before exiting
            delay(20000);

            //stop continuous measuring
            ads1115.stopFastContinuousReading();
        }


        //deregister all handlers
        ads1115.deregisterAll();

        pi4j.shutdown();

        //end test
        System.out.println("Continuous fast read test done.");
    }
}
