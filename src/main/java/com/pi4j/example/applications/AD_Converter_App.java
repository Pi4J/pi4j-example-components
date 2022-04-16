package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.AD_Converter;
import com.pi4j.example.components.SimpleButton;

public class AD_Converter_App implements Application {

    @Override
    public void execute(Context pi4j) {
        SimpleButton start = new SimpleButton(pi4j, 26,false);
        AD_Converter adc = new AD_Converter(pi4j);

        start.onDown(()-> System.out.println(adc.getValue()));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        sleep(15000);

        start.onDown(null);

    }
}
