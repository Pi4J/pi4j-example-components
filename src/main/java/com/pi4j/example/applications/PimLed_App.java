package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.Pim517;
import com.pi4j.example.components.PimLed;

public class PimLed_App implements Application {
    @Override
    public void execute(Context pi4j){
        logInfo("McpLed App is starting.");
        var mcp = new Pim517(pi4j);

        var led = new PimLed(pi4j, mcp.getPin(0));

        // Turn on the LED to have a defined state
        logInfo("Turn on LED.");
        led.setStateOn();
        delay(1000);

        // Make a flashing light by toggling the LED every second
        for (int i = 0; i < 10; i++) {
            logInfo("Current LED state is " + led.toggleState() +".");
            delay(1000);
        }

        // That's all so turn off the relay and quit
        led.setStateOff();
        logInfo("Turn off LED.");
        delay(2000);

        logInfo("McpLed app done.");
    }
}
