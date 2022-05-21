package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.helpers.PixelColor;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiMode;

import java.util.Arrays;

public class LEDMatrix extends Component{

    protected static final int DEFAULT_CHANNEL = 0;

    /* Minimum time to wait for reset to occur in nanoseconds. */
    private static final int LED_RESET_WAIT_TIME = 300_000;

    protected final Spi spi;
    protected final Context context;
    private final int frequency;
    private final int renderWaitTime;
    /** Brightness value between 0 and 255 */
    private int brightness;
    private final int[][] matrix;
    private int numLeds;
    private final byte[] pixelRaw;
    private long lastRenderTime;

    private final byte Bit_0 = (byte) 0b11000000;// 192 in Decimal
    private final byte Bit_1 = (byte) 0b11111000;// 248 in Decimal
    private final byte Bit_Reset = (byte) 0b00000000;// 0 in Decimal

    /**
     * Creates a new LEDMatrix with the defined Matrix.
     * You can give in something like int[3][4] or
     * matrix = {{1, -2, 3},
     * {-4, -5, 6, 9},
     * {7}}
     *
     * @param pi4j    Pi4J context
     * @param matrix How many LEDs are on this Strand
     * @param brightness How bright the leds can be at max, Range 0 - 255
     */
    public LEDMatrix(Context pi4j, int[][] matrix, int brightness) {
        this(pi4j, matrix, brightness, DEFAULT_CHANNEL);
    }

    /**
     * Creates a new LEDMatrix with the defined rows and columns
     *
     * @param pi4j      Pi4J context
     * @param Rows      How many Rows of LED
     * @param Columns   How many columns of LED
     * @param brightness How bright the leds can be at max, Range 0 - 255
     */
    public LEDMatrix(Context pi4j, int Rows, int Columns, int brightness) {
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
    public LEDMatrix(Context pi4j, int[][] matrix, int brightness, int channel) {
        this.matrix = matrix;
        this.brightness = brightness;
        this.frequency = 800_000;
        this.context = pi4j;
        this.spi = pi4j.create(buildSpiConfig(pi4j, channel, frequency));

        // Allocate SPI transmit buffer (same size as PCM)
        this.numLeds = 0;
        for (int[] ints : matrix) {
            this.numLeds += ints.length;
        }
        // The raw bytes that get sent to the ledstrip
        // 3 Color channels per led, at 8 bytes each, with 2 reset bytes
        pixelRaw = new byte[(3*numLeds*8)+2];

        // 1.25us per bit (1250ns)
        renderWaitTime = numLeds * 3 * 8 * 1250 + LED_RESET_WAIT_TIME;
    }

    /**
     * Builds a new SPI instance for the LED matrix
     *
     * @param pi4j Pi4J context
     * @return SPI instance
     */
    private SpiConfig buildSpiConfig(Context pi4j, int channel, int frequency) {
        return Spi.newConfigBuilder(pi4j)
                .id("SPI" + 1)
                .name("LED Matrix")
                .address(channel)
                .mode(SpiMode.MODE_0)
                .baud(8*frequency) //bitbanging from Bit to SPI-Byte
                .build();
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
        logInfo("Turning all leds off before close");
        allOff();
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

    //TODO maybe add function to write? or something like that

    /**
     * Pixels are sent as follows: - The first transmitted pixel is the pixel
     * closest to the transmitter. - The most significant bit is always sent first.
     * <p>
     * g7,g6,g5,g4,g3,g2,g1,g0,r7,r6,r5,r4,r3,r2,r1,r0,b7,b6,b5,b4,b3,b2,b1,b0
     * \_____________________________________________________________________/ |
     * _________________... | / __________________... | / / ___________________... |
     * / / / GRB,GRB,GRB,GRB,...
     *
     */
    public void render() {
        //beginning at 1, because the first byte is a reset
        int counter = 1;
        for (int x = 0; x < matrix.length; x++) {
            for (int i = 0; i < matrix[x].length; i++) {

                //Scaling the color to the max brightness
                matrix[x][i] = PixelColor.setRedComponent(matrix[x][i], PixelColor.getRedComponent(matrix[x][i])*brightness/256);
                matrix[x][i] = PixelColor.setGreenComponent(matrix[x][i], PixelColor.getGreenComponent(matrix[x][i])*brightness/256);
                matrix[x][i] = PixelColor.setBlueComponent(matrix[x][i], PixelColor.getBlueComponent(matrix[x][i])*brightness/256);

                /* Calculatin GRB from RGB */
                for (int j = 15; j >= 8; j--) {
                    if(((matrix[x][i] >> j) & 1) == 1){
                        pixelRaw[counter++] = Bit_1;
                    }else{
                        pixelRaw[counter++] = Bit_0;
                    }
                }
                for (int j = 23; j >= 16; j--) {
                    if(((matrix[x][i] >> j) & 1) == 1){
                        pixelRaw[counter++] = Bit_1;
                    }else{
                        pixelRaw[counter++] = Bit_0;
                    }
                }
                for (int j = 7; j >= 0; j--) {
                    if(((matrix[x][i] >> j) & 1) == 1){
                        pixelRaw[counter++] = Bit_1;
                    }else{
                        pixelRaw[counter++] = Bit_0;
                    }
                }
            }
        }

        // While bitbanging, the first and last byte have to be a reset
        pixelRaw[0] = Bit_Reset;
        pixelRaw[pixelRaw.length-1] = Bit_Reset;

        // waiting since last render time
        if (lastRenderTime != 0) {
            int diff = (int) (System.nanoTime() - lastRenderTime);
            if(renderWaitTime - diff > 0) {
                int millis = (renderWaitTime - diff) / 1_000_000;
                int nanos = (renderWaitTime - diff) % 1_000_000;
                sleep(millis, nanos);
            }
        }

        //writing on the PIN
        spi.write(pixelRaw);

        logger.info("finished rendering");
        lastRenderTime = System.nanoTime();
    }

    /**
     * setting all leds off
     */
    public void allOff() {
        for (int i = 0; i < matrix.length; i++) {
            Arrays.fill(matrix[i], 0);
        }
        render();
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
    public int getBrightness(){return this.brightness;}

    /**
     * Set the brightness of all LED's
     *
     * @param brightness new max. brightness
     */
    public void setBrightness(int brightness){this.brightness = brightness;}
}
