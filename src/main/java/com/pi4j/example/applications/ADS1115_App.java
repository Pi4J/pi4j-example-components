package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;

import static java.lang.Thread.sleep;

public class ADS1115_App implements Application {

    @Override
    public void execute(Context pi4j) {
        logInfo("ADS1115 test started ...");
        //read all chanel in single mode
        singleRead(pi4j);

        //read on chanel in continious mode
        continiousRead(pi4j);
        logInfo("ADS1115 test done");
    }

    private void singleRead(Context pi4j){
        //start test
        logInfo("Single read started ...");
        logInfo("Create ADS1115 object");
        ADS1115 adc = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND);

        //read analog value from all four channels
        for (int i = 0; i < 30; i++) {
            double aIn0 = adc.singleShotAIn0();
            double aIn1 = adc.singleShotAIn1();
            double aIn2 = adc.singleShotAIn2();
            double aIn3 = adc.singleShotAIn3();
            logInfo("[" + i + "] Voltages: a0=" + String.format("%.3f", aIn0)
                    + " V, a1=" + String.format("%.3f", aIn1)
                    + " V, a2=" + String.format("%.3f", aIn2)
                    + " V, a3=" + String.format("%.3f", aIn3) + " V");

            //wait for next read
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                logInfo("Error: " + e);
            }
        }

        //end test
        logInfo("Single read done.");
    }

    private void continiousRead(Context pi4j){
        //start test
        logInfo("Continious read test started ...");
        logInfo("create ads instance");
        ADS1115 ads1115 = new ADS1115(pi4j);

        // Register event handlers to print a message on value change
        ads1115.setRunnable(()->{System.out.println("The actual value is: "
                + String.format("%.3f", ads1115.continiousReadAI()) + "voltage.");});

        //start continious measuring
        ads1115.startContiniousReading(ADS1115.MUX.AIN0_GND, 20, 1);

        // Wait while handling events before exiting
        try {
            sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //stop continious measuring
        ads1115.stopContiniousReading();

        //end test
        logInfo("Continious read test done.");
    }
}
