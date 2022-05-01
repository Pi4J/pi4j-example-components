package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADS1115_App implements Application {

    private static final Logger LOG = LoggerFactory.getLogger(ADS1115.class);

    @Override
    public void execute(Context pi4j) {

        LOG.info("ADS1115Test started ...");
        LOG.info("Create ADS1115 object");
        ADS1115 adc = new ADS1115(pi4j, 0x1, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND);

        for (int i = 0; i < 100; i++) {
            double aIn0 = adc.getAIn0();
            double aIn1 = adc.getAIn1();
            double aIn2 = adc.getAIn2();
            double aIn3 = adc.getAIn3();
            LOG.info("[{}] Voltages: a0={} V, a1={} V, a2={} V, a3={} V",
                    i, String.format("%.3f", aIn0), String.format("%.3f", aIn1), String.format("%.3f", aIn2), String.format("%.3f", aIn3));
            sleep(500);
        }

        LOG.info("ADS1115Test done.");



    }
}
