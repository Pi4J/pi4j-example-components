package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.LEDMatrix;
import com.pi4j.example.components.helpers.PixelColor;

public class LEDMatrix_App implements Application {
    @Override
    public void execute(Context pi4j) {
        int Rows = 3;
        int Columns = 3;
        int brightness = 127;

        LEDMatrix ledMatrix = new LEDMatrix(pi4j, Rows, Columns, brightness);

        ledMatrix.allOff();

        ledMatrix.setMatrixColor(PixelColor.RED);
        delay(3000);

        ledMatrix.setStripColor(1, PixelColor.GREEN);
        delay(3000);

        ledMatrix.setPixelColor(2, 2, PixelColor.YELLOW);
        delay(3000);

        ledMatrix.close();
    }
}
