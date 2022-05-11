package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;

import static java.lang.Thread.sleep;

public class ADS1115_App implements Application {

    @Override
    public void execute(Context pi4j) {

        logInfo("ADS1115Test started ...");
        logInfo("Create ADS1115 object");
        ADS1115 adc = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND);

        for (int i = 0; i < 100; i++) {
            double aIn0 = adc.singleShotAIn0();
            double aIn1 = adc.singleShotAIn1();
            double aIn2 = adc.singleShotAIn2();
            double aIn3 = adc.singleShotAIn3();
            logInfo("[" + i + "] Voltages: a0=" + String.format("%.3f", aIn0)
                            + " V, a1=" + String.format("%.3f", aIn1)
                            + " V, a2=" + String.format("%.3f", aIn2)
                            + " V, a3=" + String.format("%.3f", aIn3) + " V");
            try {
                sleep(500);
            } catch (InterruptedException e) {
                logInfo("Error: " + e);
            }
        }

        logInfo("ADS1115Test done.");



    }
}
