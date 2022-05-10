package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.helpers.PIN;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;

import java.util.Arrays;

public class LEDStrip extends Component{
    protected final Spi spi;

    private static final int LED_COLOURS = 4;
    private static final int LED_RESET_uS = 55;
    /* Minimum time to wait for reset to occur in nanoseconds. */
    private static final int LED_RESET_WAIT_TIME = 300_000;

    // Symbol definitions
    private static final byte SYMBOL_HIGH = 0b110;
    private static final byte SYMBOL_LOW = 0b100;
    // Symbol definitions for software inversion
    private static final byte SYMBOL_HIGH_INV = 0b001;
    private static final byte SYMBOL_LOW_INV = 0b011;

    private int numLeds;
    private long lastRenderTime;
    private int renderWaitTime;
    // Brightness value between 0 and 255
    private int brightness;
    private int[] leds;
    private byte[] gamma;
    private byte[] pixelRaw;

    /**
     * Creates a new simpleLed component with a custom BCM pin.
     *
     * @param pi4j    Pi4J context
     * @param address Custom BCM pin address
     * @param numLeds    How many LEDs are on this Strand
     */
    public LEDStrip(Context pi4j, PIN address, int numLeds, int brightness) {
        logger.info("initialising a ledstrip with "+numLeds+" leds");
        this.spi = pi4j.create(buildSpiConfig(pi4j));
        this.numLeds = numLeds;
        this.leds = new int[numLeds];
        this.gamma = new byte[256];
        this.brightness = brightness;

        // Set default uncorrected gamma table
        for (int x = 0; x < 256; x++) {
            gamma[x] = (byte) x;
        }

        // Allocate SPI transmit buffer (same size as PCM)
        pixelRaw = new byte[PCM_BYTE_COUNT(numLeds, 800_000)];

        // 1.25us per bit (1250ns)
        renderWaitTime = numLeds * 3 * 8 * 1250 + LED_RESET_WAIT_TIME;
    }

    /**
     * Builds a new SPI instance for the LED matrix
     *
     * @param pi4j    Pi4J context
     * @return SPI instance
     */
    private static SpiConfig buildSpiConfig(Context pi4j) {
        return Spi.newConfigBuilder(pi4j)
                .id("SPI" + 1)
                .name("LED Matrix")
                .address(1)
                .baud(800000)
                .build();
    }


    public void close() {
        logger.info("Turning all leds off before close");
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
    public void setStripColor(int color){
        Arrays.fill(leds, color);
    }

    /**
     * Pixels are sent as follows: - The first transmitted pixel is the pixel
     * closest to the transmitter. - The most significant bit is always sent first.
     *
     * g7,g6,g5,g4,g3,g2,g1,g0,r7,r6,r5,r4,r3,r2,r1,r0,b7,b6,b5,b4,b3,b2,b1,b0
     * \_____________________________________________________________________/ |
     * _________________... | / __________________... | / / ___________________... |
     * / / / GRB,GRB,GRB,GRB,...
     *
     * For BYTE_ORDER_RGB, the order of the first two bytes are reversed.
     */
    public void render() {
        int bitpos = 7;
        int bytepos = 0;
        int scale = brightness + 1;
        int color_count = 3;

        for (int i = 0; i < numLeds; i++) {
            // Swap the colors around based on the led strip type
            byte[] colour = { gamma[(((leds[i] >> 8) & 0xff) * scale) >> 8],
                    gamma[(((leds[i] >> 16) & 0xff) * scale) >> 8],
                    gamma[(((leds[i] >> 0) & 0xff) * scale) >> 8],
                    gamma[(((leds[i] >> 0) & 0xff) * scale) >> 8] };

            // Color
            for (int j = 0; j < color_count; j++) {
                // Bit
                for (int k = 7; k >= 0; k--) {
                    int symbol = ((colour[j] & (1 << k)) != 0) ? SYMBOL_HIGH : SYMBOL_LOW;

                    // Symbol
                    for (int l = 2; l >= 0; l--) {
                        /*
                         * volatile byte *byteptr = &pxl_raw[bytepos]; byteptr &= ~(1 << bitpos); if
                         * (symbol & (1 << l)) { byteptr |= (1 << bitpos); }
                         */
                        pixelRaw[bytepos] &= ~(1 << bitpos);
                        if ((symbol & (1 << l)) != 0) {
                            pixelRaw[bytepos] |= (1 << bitpos);
                        }

                        bitpos--;
                        if (bitpos < 0) {
                            bytepos++;
                            bitpos = 7;
                        }
                    }
                }
            }
        }

        if (lastRenderTime != 0) {
            int diff = (int) (System.nanoTime() - lastRenderTime);
            if (renderWaitTime > diff) {
                delay(renderWaitTime - diff);
            }
        }

        //TODO Call Write
        for (byte led:pixelRaw) {
            execute((byte)0x01, led);
        }
        logger.info("finished rendering");
        lastRenderTime = System.nanoTime();
    }

    /**
     * setting all leds off
     */
    public void allOff() {
        Arrays.fill(leds, 0);
        render();
    }

    private static final int LED_BIT_COUNT(int numLeds, int frequency) {
        return (numLeds * LED_COLOURS * 8 * 3) + ((LED_RESET_uS * (frequency * 3)) / 1000000);
    }

    private static final int PCM_BYTE_COUNT(int numLeds, int frequency) {
        return (((LED_BIT_COUNT(numLeds, frequency) >> 3) & ~0x7) + 4) + 4;
    }

    /**
     * Helper method for sending a command to the chip with data. Communication happens over SPI by simply sending two pieces of
     * data, more specifically the desired command as a byte value, followed by the data as another byte value.
     *
     * @param command Command to be executed
     * @param data    Data for the given command
     */
    private void execute(byte command, byte data) {
        spi.write(command, data);
    }
}
