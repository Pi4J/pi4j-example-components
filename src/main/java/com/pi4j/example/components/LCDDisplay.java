package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

/**
 * Class to provide IO interfacing with I2C Bus
 */
public class LCDDisplay extends Component{
    private int ROWS    = 2;
    private int COLUMNS = 16;

    private static final byte LCD_CLEAR_DISPLAY   = (byte) 0x01;
    private static final byte LCD_RETURN_HOME     = (byte) 0x02;
    private static final byte LCD_ENTRY_MODE_SET  = (byte) 0x04;
    private static final byte LCD_DISPLAY_CONTROL = (byte) 0x08;
    private static final byte LCD_CURSOR_SHIFT    = (byte) 0x10;
    private static final byte LCD_FUNCTION_SET    = (byte) 0x20;
    private static final byte LCD_SET_CGRAM_ADDR  = (byte) 0x40;
    private static final byte LCD_SET_DDRAM_ADDR  = (byte) 0x80;

    // flags for display entry mode
    private static final byte LCD_ENTRY_RIGHT           = (byte) 0x00;
    private static final byte LCD_ENTRY_LEFT            = (byte) 0x02;
    private static final byte LCD_ENTRY_SHIFT_INCREMENT = (byte) 0x01;
    private static final byte LCD_ENTRY_SHIFT_DECREMENT = (byte) 0x00;

    // flags for display on/off control
    private static final byte LCD_DISPLAY_ON  = (byte) 0x04;
    private static final byte LCD_DISPLAY_OFF = (byte) 0x00;
    private static final byte LCD_CURSOR_ON   = (byte) 0x02;
    private static final byte LCD_CURSOR_OFF  = (byte) 0x00;
    private static final byte LCD_BLINK_ON    = (byte) 0x01;
    private static final byte LCD_BLINK_OFF   = (byte) 0x00;

    // flags for display/cursor shift
    private static final byte LCD_DISPLAY_MOVE = (byte) 0x08;
    private static final byte LCD_CURSOR_MOVE  = (byte) 0x00;
    private static final byte LCD_MOVE_RIGHT   = (byte) 0x04;
    private static final byte LCD_MOVE_LEFT    = (byte) 0x00;

    // flags for function set
    private static final byte LCD_8BIT_MODE = (byte) 0x10;
    private static final byte LCD_4BIT_MODE = (byte) 0x00;
    private static final byte LCD_2LINE     = (byte) 0x08;
    private static final byte LCD_1LINE     = (byte) 0x00;
    private static final byte LCD_5x10DOTS  = (byte) 0x04;
    private static final byte LCD_5x8DOTS   = (byte) 0x00;

    // flags for backlight control
    private static final byte LCD_BACKLIGHT    = (byte) 0x08;
    private static final byte LCD_NO_BACKLIGHT = (byte) 0x00;

    private static final byte En = (byte) 0b000_00100; // Enable bit
    private static final byte Rw = (byte) 0b000_00010; // Read/Write bit
    private static final byte Rs = (byte) 0b000_00001; // Register select bit

    private static final int DEFAULT_BUS    = 0x1;
    private static final int DEFAULT_DEVICE = 0x27;

    private final I2C     i2c;
    private       boolean backlight;

    public LCDDisplay(Context pi4j, int ROWS, int COLUMNS) {
        this(createI2C(pi4j, DEFAULT_BUS, DEFAULT_DEVICE));
        this.ROWS = ROWS;
        this.COLUMNS = COLUMNS;
    }

    public LCDDisplay(I2C device) {
        i2c = device;
        init();
    }

    /**
     * Turns the backlight on or off
     */
    public void setDisplayBacklight(boolean state) {
        this.backlight = state;
        executeCommand(this.backlight ? LCD_BACKLIGHT : LCD_NO_BACKLIGHT);
    }

    /**
     * Clear the LCD and set cursor to home
     */
    public void clearDisplay() {
        writeCommand(LCD_CLEAR_DISPLAY);
        moveCursorHome();
    }

    /**
     * Returns the Cursor to Home Position (First line, first character)
     */
    public void moveCursorHome() {
        writeCommand(LCD_RETURN_HOME);
        sleep(3, 0);
    }

    public void off(){
        executeCommand(LCD_DISPLAY_OFF);
    }

    /**
     * writes string to the lcd display on a specific line. Excess characters (greater than Columns) are not shown on the line
     *
     * @param text to be displayed
     * @param line on the display for the text to appear
     */
    public void displayText(String text, int line) {
        displayText(text, line, 0, false);
    }

    /**
     * writes string to the lcd display. Excess characters (greater than Columns) are not shown on the line
     *
     * @param text to be displayed
     * @param line on the display for the text to appear
     * @param pos on the display for the Column
     * @param jumpToNextLine if it can make a newLine
     */
    public void displayText(String text, int line, int pos, boolean jumpToNextLine) {
        if (text.length() > 32 - pos) {
            text = text.substring(0, 31 - pos);
            logger.info("Text length cut to 31 characters");
        }

        String firstLine  = text;
        String secondLine = text;

        //lcd only has 2 lines, so I assume the first one if the second one wasn't selected explicitly
        if (line != 2) {
            boolean textLargerThanLine = text.length() > COLUMNS - pos;
            if (textLargerThanLine) {
                firstLine = text.substring(0, COLUMNS - pos);
                secondLine = text.substring(COLUMNS - pos);
            }
            displayLine(firstLine, pos);
            if (jumpToNextLine && textLargerThanLine) {
                displayLine(secondLine, pos + 0x40);
            }
        } else {
            displayLine(secondLine, pos + 0x40);
        }
    }

    public void displayText(String text){
        if ((text.length() > (ROWS*COLUMNS) && !text.contains("\n")) || (text.length() > (ROWS*COLUMNS)+1 && text.contains("\n"))) {
            throw new IllegalArgumentException("Too long text. Only " + ROWS*COLUMNS +" characters plus linebreaks allowed");
        }

        // Clean and prepare to write some text
        var currentLine = 0;
        String[] lines = new String[ROWS+1];
        clearDisplay();

        // Iterate through lines and characters and write them to the display
        for (int i = 0; i < text.length(); i++) {
            // line break in text found
            if (text.charAt(i) == '\n' && currentLine < ROWS) {
                currentLine++;
                continue;
            }

            // Write character to array
            lines[currentLine] += (char)text.charAt(i);

            if(lines[currentLine].length() == COLUMNS && currentLine < ROWS){
                currentLine++;
            }

            for(int j = 0; j < ROWS; j++){
                if(lines[j] != null && lines[j].length() > 0) {
                    displayLine(lines[j], j * 0x40);
                }
            }
        }
    }

    /**
     * write a character to lcd
     */
    public void writeCharacter(char charvalue) {
        writeSplitCommand((byte) charvalue, Rs);
    }

    /**
     * displays a line on a specific position
     *
     * @param text to display
     * @param pos  for the start of the text
     */
    private void displayLine(String text, int pos) {
        writeCommand((byte) (0x80 + pos));

        for (int i = 0; i < text.length(); i++) {
            writeCharacter(text.charAt(i));
        }
    }

    /**
     * Write a command to the LCD
     */
    private void writeCommand(byte cmd) {
        writeSplitCommand(cmd, (byte) 0);
    }

    private void writeSplitCommand(byte cmd, byte mode) {
        //bitwise AND with 11110000 to remove last 4 bits
        writeFourBits((byte) (mode | (cmd & 0xF0)));
        //bitshift and bitwise AND to remove first 4 bits
        writeFourBits((byte) (mode | ((cmd << 4) & 0xF0)));
    }

    private void writeFourBits(byte data) {
        i2c.write((byte) (data | (backlight ? LCD_BACKLIGHT : LCD_NO_BACKLIGHT)));
        lcd_strobe(data);
    }

    /**
     * Clocks EN to latch command
     */
    private void lcd_strobe(byte data) {
        i2c.write((byte) (data | En | (backlight ? LCD_BACKLIGHT : LCD_NO_BACKLIGHT)));
        sleep(0, 500_000);
        i2c.write((byte) ((data & ~En) | (backlight ? LCD_BACKLIGHT : LCD_NO_BACKLIGHT)));
        sleep(0, 100_000);
    }


    /**
     * Execute Display commands
     *
     * @param command Select the LCD Command
     * @param data    Setup command data
     */
    private void executeCommand(byte command, byte data) {
        executeCommand((byte) (command | data));
    }

    /**
     * Write a single command
     */
    private void executeCommand(byte cmd) {
        i2c.write(cmd);
        sleep(0, 100_000);
    }

    /**
     * Initializes the LCD with the backlight off
     */
    private void init() {
        writeCommand((byte) 0x03);
        writeCommand((byte) 0x03);
        writeCommand((byte) 0x03);
        writeCommand((byte) 0x02);

        // Initialize display settings
        writeCommand((byte) (LCD_FUNCTION_SET | LCD_2LINE | LCD_5x8DOTS | LCD_4BIT_MODE));
        writeCommand((byte) (LCD_DISPLAY_CONTROL | LCD_DISPLAY_ON | LCD_CURSOR_OFF | LCD_BLINK_OFF));
        writeCommand((byte) (LCD_ENTRY_MODE_SET | LCD_ENTRY_LEFT | LCD_ENTRY_SHIFT_DECREMENT));

        clearDisplay();

        // Enable backlight
        setDisplayBacklight(true);
        logger.info("LCD Display initialized");
    }


    private static I2C createI2C(Context pi4j, int bus, int device) {
        I2CProvider i2CProvider = pi4j.getI2CProvider();
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("I2C-" + device + "@" + bus)
                .name("PCF8574AT")
                .bus(bus)
                .device(device)
                .build();
        return i2CProvider.create(i2cConfig);
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
}
