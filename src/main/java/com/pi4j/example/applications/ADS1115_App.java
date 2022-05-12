package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;

public class ADS1115_App implements Application {

    @Override
    public void execute(Context pi4j) {

        logInfo("ADS1115 app started ...");
        ADS1115 adc = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND);

        for (int i = 0; i < 100; i++) {
            double aIn0 = adc.getAIn0();
            double aIn1 = adc.getAIn1();
            double aIn2 = adc.getAIn2();
            double aIn3 = adc.getAIn3();
            logInfo("["+i+"] Voltages: a0=" + String.format("%.3f", aIn0) + " V, a1=" + String.format("%.3f", aIn1) +
                            " V, a2=" + String.format("%.3f", aIn2) + " V, a3=" + String.format("%.3f", aIn3) + " V");
            delay(500);
        }

        logInfo("ADS1115 app done.");
    }
}
