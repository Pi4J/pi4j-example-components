package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.helpers.PixelColor;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiMode;

import java.util.Arrays;

public class LEDStrip extends Component {

    protected static final int DEFAULT_CHANNEL   = 0;
    /* Minimum time to wait for reset to occur in nanoseconds. */
    private static final int LED_RESET_WAIT_TIME = 300_000;

    protected final Spi spi;
    private final int numLeds;
    private final int frequency;
    private final int renderWaitTime;
    /** Brightness value between 0 and 255 */
    private int brightness;
    private final int[] leds;
    private final byte[] pixelRaw;
    private long lastRenderTime;

    private final byte Bit_0     = (byte) 0b11000000;// 192 in Decimal
    private final byte Bit_1     = (byte) 0b11111000;// 248 in Decimal
    private final byte Bit_Reset = (byte) 0b00000000;// 0 in Decimal

    /**
     * Creates a new simpleLed component with a custom BCM pin.
     *
     * @param pi4j    Pi4J context
     * @param numLeds How many LEDs are on this Strand
     * @param brightness How bright the leds can be at max, Range 0 - 255
     */
    public LEDStrip(Context pi4j, int numLeds, int brightness) {
        this(pi4j, numLeds, brightness, DEFAULT_CHANNEL);
    }

    /**
     * Creates a new simpleLed component with a custom BCM pin.
     *
     * @param pi4j    Pi4J context
     * @param numLeds How many LEDs are on this Strand
     * @param brightness How bright the leds can be at max, Range 0 - 255
     * @param channel which channel to use
     */
    public LEDStrip(Context pi4j, int numLeds, int brightness, int channel) {
        logDebug("initialising a ledstrip with " + numLeds + " leds");
        this.numLeds    = numLeds;
        this.leds       = new int[numLeds];
        this.brightness = brightness;
        this.frequency  = 800_000;
        this.spi = pi4j.create(buildSpiConfig(pi4j, channel, frequency));

        // Allocate SPI transmit buffer (same size as PCM)
        pixelRaw = new byte[3*numLeds*8];

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
     * Setting all LEDS off and closing the strip
     */
    public void close() {
        logDebug("Turning all leds off before close");
        allOff();
    }

    /**
     * function to get the amount of the leds on the strip
     *
     * @return int with the amount of pixels
     */
    public int getNumPixels() {
        return numLeds;
    }

    /**
     * function to get the color (as an int) of a specified led
     *
     * @param pixel which position on the ledstrip, range 0 - numLEDS-1
     * @return the color of the specified led on the strip
     */
    public int getPixelColor(int pixel) {
        return leds[pixel];
    }

    /**
     * setting the color of a specified led on the strip
     *
     * @param pixel which position on the strip, range 0 - numLEDS-1
     * @param color the color that is set
     */
    public void setPixelColor(int pixel, int color) {
        leds[pixel] = color;
    }

    /**
     * Setting all leds to the same color
     *
     * @param color the color that is set
     */
    public void setStripColor(int color) {
        Arrays.fill(leds, color);
    }

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
        int counter = 0;
        for (int i = 0; i < numLeds; i++) {

            //Scaling the color to the max brightness
            leds[i] = PixelColor.setRedComponent(leds[i], PixelColor.getRedComponent(leds[i])*brightness/256);
            leds[i] = PixelColor.setGreenComponent(leds[i], PixelColor.getGreenComponent(leds[i])*brightness/256);
            leds[i] = PixelColor.setBlueComponent(leds[i], PixelColor.getBlueComponent(leds[i])*brightness/256);

            /* Calculatin GRB from RGB */
            for (int j = 15; j >= 8; j--) {
                if(((leds[i] >> j) & 1) == 1){
                    pixelRaw[counter++] = Bit_1;
                }else{
                    pixelRaw[counter++] = Bit_0;
                }
            }
            for (int j = 23; j >= 16; j--) {
                if(((leds[i] >> j) & 1) == 1){
                    pixelRaw[counter++] = Bit_1;
                }else{
                    pixelRaw[counter++] = Bit_0;
                }
            }
            for (int j = 7; j >= 0; j--) {
                if(((leds[i] >> j) & 1) == 1){
                    pixelRaw[counter++] = Bit_1;
                }else{
                    pixelRaw[counter++] = Bit_0;
                }
            }
        }

        // While bitbanging, the first and last byte have to be a reset
        byte[] bytes = new byte[pixelRaw.length + 2];
        System.arraycopy(pixelRaw, 0, bytes, 1, pixelRaw.length);
        bytes[0] = Bit_Reset;
        bytes[bytes.length-1] = Bit_Reset;

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
        spi.write(bytes);

        logDebug("finished rendering");
        lastRenderTime = System.nanoTime();
    }

    /**
     * setting all leds off
     */
    public void allOff() {
        Arrays.fill(leds, 0);
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
