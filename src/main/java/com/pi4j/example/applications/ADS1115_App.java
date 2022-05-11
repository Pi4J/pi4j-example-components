package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;

public class ADS1115_App implements Application {

    @Override
    public void execute(Context pi4j) {

        logger.info("ADS1115Test started ...");
        logger.info("Create ADS1115 object");
        ADS1115 adc = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND);

        for (int i = 0; i < 100; i++) {
            double aIn0 = adc.singleShotAIn0();
            double aIn1 = adc.singleShotAIn1();
            double aIn2 = adc.singleShotAIn2();
            double aIn3 = adc.singleShotAIn3();
            logger.info("[{}] Voltages: a0={} V, a1={} V, a2={} V, a3={} V",
                    i, String.format("%.3f", aIn0), String.format("%.3f", aIn1), String.format("%.3f", aIn2), String.format("%.3f", aIn3));
            sleep(500);
        }

        logger.info("ADS1115Test done.");



    }
}
