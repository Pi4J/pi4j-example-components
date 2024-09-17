package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import com.pi4j.catalog.components.LedMatrix;
import com.pi4j.catalog.components.LedStrip;

/**
 * This example shows how to use the LEDMatrix component by setting the LEDs on the strips to different colors
 * <p>
 * see <a href="https://pi4j.com/examples/components/ledmatrix/">Description on Pi4J website</a>
 */
public class LedMatrixApp {
    public static void main(String[] args) {
        final Context pi4j = Pi4J.newAutoContext();

        System.out.println("LED matrix demo started ...");
        int rows = 2;
        int columns = 5;

        System.out.println("Initialising the matrix");
        LedMatrix ledMatrix = new LedMatrix(pi4j, rows, columns);

        System.out.println("whole matrix Red.");
        ledMatrix.setMatrixColor(LedStrip.LedColor.RED);
        ledMatrix.render(Duration.ofSeconds(3));

        System.out.println("First row is GREEN");
        ledMatrix.allOff();
        ledMatrix.setRowColor(0, LedStrip.LedColor.GREEN);
        ledMatrix.render(Duration.ofSeconds(3));

        System.out.println("Second row is GREEN");
        ledMatrix.allOff();
        ledMatrix.setRowColor(1, LedStrip.LedColor.GREEN);
        ledMatrix.render(Duration.ofSeconds(3));

        System.out.println("Second column is BLUE");
        ledMatrix.allOff();
        ledMatrix.setColumnColor(1, LedStrip.LedColor.BLUE);
        ledMatrix.render(Duration.ofSeconds(3));

        System.out.println("Third led of the first and second row is YELLOW");
        ledMatrix.allOff();
        ledMatrix.setPixelColor(0, 2, LedStrip.LedColor.YELLOW);
        ledMatrix.setPixelColor(1, 2, LedStrip.LedColor.YELLOW);
        ledMatrix.render(Duration.ofSeconds(3));

        ledMatrix.reset();

        System.out.println("LED matrix app finished.");
    }
}
