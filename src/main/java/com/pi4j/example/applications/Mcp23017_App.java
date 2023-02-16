package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.Mcp23017;
import com.pi4j.example.components.McpLed;

public class Mcp23017_App implements Application {
    @Override
    public void execute(Context pi4j) {

        /**
         * Example with no interrupt handling
         */
        var mcp = new Mcp23017(pi4j);

        var digitalInput = mcp.getPin(0);

        var digitalOutput = mcp.getPin(1);

        digitalInput.setDirection(Mcp23017.DigitalInOut.Direction.INPUT);
        digitalInput.setPullup(true);
        digitalInput.invert_polarity(false);

        digitalOutput.setDirection(Mcp23017.DigitalInOut.Direction.OUTPUT);

        int times = 0;
        while(times < 5){
            if(!digitalOutput.value()){
                System.out.println("Button pressed");

                digitalInput.setValue(true);
                times++;
            }else{
                digitalInput.setValue(false);
            }
        }

        //clear the interrupt register
        mcp.clear_ints();
    }

    public void execute(Context pi4j, int x){
        /**
         * Test with interrupt handling
         */
        var mcp = new Mcp23017(pi4j);
    }

    public void execute(Context pi4j, boolean x){
        /**
         * Test with McpLED
         */
        var mcp = new Mcp23017(pi4j);

        var led = new McpLed(pi4j, mcp.getPin(0));

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

        logInfo("Simple LED app done.");
    }
}
