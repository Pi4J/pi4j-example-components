package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.ADS1115;

import static java.lang.Thread.sleep;

public class PotentiometerApp implements Application {

    @Override
    public void execute(Context pi4j) {
        System.out.println("create ads instance");
        ADS1115 ads1115 = new ADS1115(pi4j);

        System.out.println("set onValueChange");
        ads1115.setOnValueChange(()->{System.out.println("The actual value is: "
                + String.format("%.3f", ads1115.continiousReadAI()) + "voltage.");});
        ads1115.startContiniousReading(ADS1115.MUX.AIN0_GND, 20, 1);
        try {
            sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ads1115.stopContiniousReading();
    }
}
