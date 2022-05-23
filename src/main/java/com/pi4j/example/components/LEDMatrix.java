package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiMode;

import java.util.Arrays;

public class LEDMatrix extends Component{

    protected static final int DEFAULT_CHANNEL = 0;

    protected final Spi spi;
    protected final Context context;

    /** Brightness value between 0 and 1 */
    private double brightness;
    private final int[][] matrix;
    private int numLeds;

    private final LEDStrip ledStrip;

    /**
     * Creates a new LEDMatrix with the defined Matrix.
     * You can give in something like int[3][4] or
     * matrix = {{0, 0, 0},
     * {0, 0, 0, 0},
     * {0}}
     *
     * @param pi4j    Pi4J context
     * @param matrix How many LEDs are on this Strand
     * @param brightness How bright the leds can be at max, Range 0 - 1
     */
    public LEDMatrix(Context pi4j, int[][] matrix, double brightness) {
        this(pi4j, matrix, brightness, DEFAULT_CHANNEL);
    }

    /**
     * Creates a new LEDMatrix with the defined rows and columns
     *
     * @param pi4j      Pi4J context
     * @param Rows      How many Rows of LED
     * @param Columns   How many columns of LED
     * @param brightness How bright the leds can be at max, Range 0 - 1
     */
    public LEDMatrix(Context pi4j, int Rows, int Columns, double brightness) {
        this(pi4j, new int[Rows][Columns], brightness, DEFAULT_CHANNEL);
    }

    /**
     * Creates a new simpleLed component with a custom BCM pin.
     *
     * @param pi4j    Pi4J context
     * @param matrix How many LEDs are on this Strand
     * @param brightness How bright the leds can be at max, Range 0 - 255
     * @param channel which channel to use
     */
    public LEDMatrix(Context pi4j, int[][] matrix, double brightness, int channel) {
        this.matrix = matrix;
        this.brightness = brightness;
        this.context = pi4j;

        // Allocate SPI transmit buffer (same size as PCM)
        this.numLeds = 0;
        for (int[] ints : matrix) {
            this.numLeds += ints.length;
        }

        this.ledStrip = new LEDStrip(pi4j, numLeds, brightness, channel);
        this.spi = ledStrip.spi;
    }

    /**
     * @return the pi4j context
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Setting all LEDS off and closing the strip
     */
    public void close() {
        ledStrip.close();
    }

    /**
     * function to get the amount of the leds in the matrix
     *
     * @return int with the amount of leds over all
     */
    public int getNumPixels() {
        return numLeds;
    }

    /**
     * function to get the amount of the leds in the specified strip
     *
     * @return int with the amount of leds over all
     */
    public int getNumPixels(int strip) {
        if (strip > matrix.length || strip < 0) {
            throw new IllegalArgumentException("the strip specified does not exist");
        }
        return matrix[strip].length;
    }

    /**
     * function to get the color (as an int) of a specified led
     *
     * @param pixel which position on the ledstrip, range 0 - numLEDS-1
     * @return the color of the specified led on the strip
     */
    public int getPixelColor(int strip, int pixel) {
        if (strip > matrix.length || strip < 0 || pixel > matrix[strip].length || pixel < 0) {
            throw new IllegalArgumentException("the strip or led specified does not exist");
        }
        return matrix[strip][pixel];
    }

    /**
     * setting the color of a specified led on the strip
     *
     * @param pixel which position on the strip, range 0 - numLEDS-1
     * @param color the color that is set
     */
    public void setPixelColor(int strip, int pixel, int color) {
        if (strip > matrix.length || strip < 0 || pixel > matrix[strip].length || pixel < 0) {
            throw new IllegalArgumentException("the strip or led specified does not exist");
        }
        matrix[strip][pixel] = color;
    }

    /**
     * Setting all leds of a Row to the same color
     *
     * @param color the color that is set
     */
    public void setStripColor(int strip, int color) {
        if (strip > matrix.length || strip < 0) {
            throw new IllegalArgumentException("the strip specified does not exist");
        }
        Arrays.fill(matrix[strip], color);
    }

    /**
     * Setting all leds to the same color
     *
     * @param color the color that is set
     */
    public void setMatrixColor(int color) {
        for (int[] ints : matrix) {
            Arrays.fill(ints, color);
        }
    }

    /**
     * Rendering the LEDs by setting the pixels on the ledstrip component
     */
    public void render() {
        int counter = 0;
        ledStrip.setBrightness(brightness);
        for (int[] ints : matrix) {
            for (int anInt : ints) {
                ledStrip.setPixelColor(counter++, anInt);
            }
        }
        ledStrip.render();
    }

    /**
     * setting all leds off
     */
    public void allOff() {
        ledStrip.allOff();
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds. An {@link InterruptedException} will be catched and ignored while setting the
     * interrupt flag again.
     */
    protected void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @return the current brightness
     */
    public double getBrightness(){return this.brightness;}

    /**
     * Set the brightness of all LED's
     *
     * @param brightness new max. brightness, range 0 - 1
     */
    public void setBrightness(double brightness){this.brightness = brightness;}
}
