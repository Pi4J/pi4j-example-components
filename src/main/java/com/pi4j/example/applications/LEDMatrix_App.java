package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.LEDMatrix;
import com.pi4j.example.components.LEDStrip;

/**
 * This example shows how to use the LEDMatrix component by setting the LEDs on the strips to different colors
 */
public class LEDMatrix_App implements Application {
    @Override
    public void execute(Context pi4j) {
        int Rows = 3;
        int Columns = 3;
        double brightness = 0.5;

        System.out.println("Initialising the matrix");
        LEDMatrix ledMatrix = new LEDMatrix(pi4j, Rows, Columns, brightness);

        System.out.println("Setting all LEDs to Red.");
        ledMatrix.setMatrixColor(LEDStrip.PixelColor.RED);
        ledMatrix.render();
        delay(3000);

        System.out.println("setting the second strip to green");
        ledMatrix.setStripColor(1, LEDStrip.PixelColor.GREEN);
        ledMatrix.render();
        delay(3000);

        System.out.println("Setting the third led of the third strip to Yellow");
        ledMatrix.setPixelColor(2, 2, LEDStrip.PixelColor.YELLOW);
        ledMatrix.render();
        delay(3000);

        ledMatrix.close();
    }
}
