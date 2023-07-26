package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.Pim517;
import com.pi4j.example.components.PimButton;

public class PimButton_App implements Application {
    @Override
    public void execute(Context pi4j){
        logInfo("McpButton App is starting");
        var mcp = new Pim517(pi4j);

        var button = new PimButton(pi4j, mcp.getPin(0), false);

        // Register event handlers to print a message when pressed (onDown) and depressed (onUp)
        button.onDown      (() -> logInfo("Pressing the button"));
        button.whilePressed(() -> logInfo("Pressing"), 1000);
        button.onUp        (() -> logInfo("Stopped pressing."));

        // Wait for 15 seconds while handling events before exiting
        System.out.println("Press the button to see it in action!");
        delay(15_000);

        // Unregister all event handlers to exit this application in a clean way
        button.deRegisterAll();

        /*
        if you want to deRegister only a single function, you can do so like this:
        button.onUp(null);
        */

        logInfo("McpButton app done.");
    }
}
