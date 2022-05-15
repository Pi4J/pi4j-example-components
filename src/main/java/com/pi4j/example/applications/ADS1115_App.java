package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;

import static java.lang.Thread.sleep;

public class ADS1115_App implements Application {

    @Override
    public void execute(Context pi4j) {
        System.out.println("ADS1115 test started ...");
        //read all chanel in single mode
        //singleRead(pi4j);

        //read all chanel in continious mode
        //continiousSlowRead(pi4j);

        //read one chanel in fast continious mode
        continiousFastRead(pi4j);

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
            logInfo("[" + i + "] Voltages: a0=" + String.format("%.3f", aIn0) + " V, a1=" + String.format("%.3f", aIn1) + " V, a2=" + String.format("%.3f", aIn2) + " V, a3=" + String.format("%.3f", aIn3) + " V");
            //wait for next read
            delay(1000);
        }

        pi4j.shutdown();

        //end test
        System.out.println("Single read done.");
    }

    private void continiousSlowRead(Context pi4j) {
        //start test
        System.out.println("Continious slow read test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        // Register event handlers to print a message on value change
        ads1115.setRunnableSlowReadChannel0(() -> {
            System.out.println("The actual value from channel 0 is: " + String.format("%.3f", ads1115.getSlowContiniousReadAIn0()) + "voltage.");
        });
        ads1115.setRunnableSlowReadChannel1(() -> {
            System.out.println("The actual value from channel 1 is: " + String.format("%.3f", ads1115.getSlowContiniousReadAIn1()) + "voltage.");
        });
        ads1115.setRunnableSlowReadChannel2(() -> {
            System.out.println("The actual value from channel 2 is: " + String.format("%.3f", ads1115.getSlowContiniousReadAIn2()) + "voltage.");
        });
        ads1115.setRunnableSlowReadChannel3(() -> {
            System.out.println("The actual value from channel 3 is: " + String.format("%.3f", ads1115.getSlowContiniousReadAIn3()) + "voltage.");
        });

        //start continious measuring
        ads1115.startSlowContiniousReading(0.1, 10);

        // Wait while handling events before exiting
        delay(30000);

        //stop continious measuring
        ads1115.stopSlowReadContiniousReading();

        //deregister all handlers
        ads1115.deregisterAll();

        pi4j.shutdown();

        //end test
        logInfo("Continious slow read test done.");
    }

    private void continiousFastRead(Context pi4j) {
        //start test
        System.out.println("Continious fast read test started ...");

        ADS1115 ads1115 = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 4);

        // Register event handlers to print a message on value change
        ads1115.setRunnableFastRead(() -> {
            System.out.println("The actual value from fast read channel is: " + String.format("%.3f", ads1115.getSlowContiniousReadAIn0()) + "voltage.");
        });

        for (int i = 0; i < 4; i++) {
            //start continious measuring
            ads1115.startFastContiniousReading(i, 0.1, 10);

            // Wait while handling events before exiting
            delay(20000);

            //stop continious measuring
            ads1115.stopFastContiniousReading();
        }


        //deregister all handlers
        ads1115.deregisterAll();

        pi4j.shutdown();

        //end test
        logInfo("Continious fast read test done.");
    }
}
