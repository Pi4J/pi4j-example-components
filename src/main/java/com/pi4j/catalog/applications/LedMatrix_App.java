package com.pi4j.catalog.applications;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.LedMatrix;
import com.pi4j.catalog.components.LedStrip;

/**
 * This example shows how to use the LEDMatrix component by setting the LEDs on the strips to different colors
 * <P>
 * see <a href="https://pi4j.com/examples/components/ledmatrix/">Description on Pi4J website</a>
 */
public class LedMatrix_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("LED matrix app started ...");
        int Rows = 3;
        int Columns = 4;
        double brightness = 0.5;

        System.out.println("Initialising the matrix");
        LedMatrix ledMatrix = new LedMatrix(pi4j, Rows, Columns, brightness);

        System.out.println("Setting all LEDs to Red.");
        ledMatrix.setMatrixColor(LedStrip.PixelColor.RED);
        ledMatrix.render();
        delay(3000);

        System.out.println("setting the second strip to green");
        ledMatrix.setStripColor(1, LedStrip.PixelColor.GREEN);
        ledMatrix.render();
        delay(3000);

        System.out.println("Setting the third led of the third strip to Yellow");
        ledMatrix.setPixelColor(2, 2, LedStrip.PixelColor.YELLOW);
        ledMatrix.render();
        delay(3000);

        ledMatrix.close();

        System.out.println("LED matrix app done.");
    }
}
