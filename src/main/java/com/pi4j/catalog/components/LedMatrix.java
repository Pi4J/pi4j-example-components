package com.pi4j.catalog.components;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.components.base.Component;

/**
 * Creates an SPI Control for Neopixel for a LED matrix consisting of a single LED Strip.
 *
 * It's more or less a pure convenience class to get an API more appropriate to operate with a matrix.
 * All calls are delegated to the LED strip
 */
public class LedMatrix extends Component {

    /**
     * The corresponding LEDStrip to the matrix. The matrix is nothing more than just an array from LEDStrips, that
     * can be calculated to a single LEDStrip
     */
    private final LedStrip ledStrip;
    private final int rows;
    private final int columns;


    /**
     * Creates a new LEDMatrix with the defined rows and columns
     *
     * @param pi4j       Pi4J context
     * @param rows       How many rows of LED
     * @param columns    How many columns of LED
     * @param brightness How bright the LEDs can be at max, Range 0 - 1
     */
    public LedMatrix(Context pi4j, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.ledStrip = new LedStrip(pi4j, rows*columns);
    }


    @Override
    public void reset() {
        super.reset();
        ledStrip.reset();
    }

    /**
     * function to get the color (as an int) of a specified LED
     *
     * @param row row in the matrix, starting with 0
     * @param column column in  matrix, starting with 0
     * @return the color of the specified LED on the strip
     */
    public int getPixelColor(int row, int column) {
       return ledStrip.getPixelColor(positionOnStrip(row, column));
    }


    /**
     * setting the color of a specified led on the strip
     *
     * @param row row in the matrix, starting with 0
     * @param column column in  matrix, starting with 0
     * @param color the color that is set
     */
    public void setPixelColor(int row, int column, int color) {
       ledStrip.setPixelColor(positionOnStrip(row, column), color);
    }


    /**
     * Setting all LEDs of a row to the same color
     *
     * @param color the color that is set
     */
    public void setRowColor(int row, int color) {
        for(int i= (row * columns); i< (row * columns) + columns; i++){
            ledStrip.setPixelColor(i, color);
        }
    }

    public void setColumnColor(int column, int color){
        for(int i = 0; i<rows; i++){
            ledStrip.setPixelColor(positionOnStrip(i, column), color);
        }
    }

    /**
     * Setting all LEDs in the matrix to the same color
     *
     * @param color the color that is set
     */
    public void setMatrixColor(int color) {
        ledStrip.setStripColor(color);
    }

    /**
     * Rendering the LEDs by setting the pixels on the lED strip component
     */
    public void render(Duration duration) {
        ledStrip.render(duration);
    }

    /**
     * setting all LEDs off
     */
    public void allOff() {
        ledStrip.allOff();
    }


    /**
     * Set the brightness of all LED's
     *
     * @param brightness new max. brightness, range 0 - 1
     */
    public void setMaxBrightness(double brightness) {
        ledStrip.setMaxBrightness(brightness);
    }

    private int positionOnStrip(int row, int col) {
        boolean even = row % 2 == 0;
        int pos;
        if(even){
            pos =  (row * columns) + col;
        }
        else {
            pos = (row * columns) + (columns - 1 - col);
        }
        System.out.printf("row %d, col %d, even %b, pos %d %n", row, col, even, pos);

        return pos;
    }

}
