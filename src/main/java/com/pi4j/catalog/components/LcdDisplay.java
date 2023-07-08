package com.pi4j.catalog.components;

import java.time.Duration;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;

import com.pi4j.catalog.components.base.I2CDevice;

/**
 * Implementation of a LCDDisplay using GPIO with Pi4J
 * For now, only works with the PCF8574T Backpack
 */
public class LcdDisplay extends I2CDevice {
    /** Flags for display commands */
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
    private static final byte LCD_BACKLIGHT     = (byte) 0x08;
    private static final byte LCD_NO_BACKLIGHT  = (byte) 0x00;
    private static final byte En                = (byte) 0b000_00100; // Enable bit
    private static final byte Rw                = (byte) 0b000_00010; // Read/Write bit
    private static final byte Rs                = (byte) 0b000_00001; // Register select bit
  
    /**
     * Display row offsets. Offset for up to 4 rows.
     */
    private static final byte[] LCD_ROW_OFFSETS = {0x00, 0x40, 0x14, 0x54};

    private static final int DEFAULT_DEVICE = 0x27;

    /**
     * The amount of rows on the display
     */
    private final int rows;
    /**
     * The amount of columns on the display
     */
    private final int columns;

    /**
     * With this Byte cursor visibility is controlled
     */
    private byte displayControl;
    /**
     * This boolean checks if the backlight is on or not
     */
    private boolean backlight;

    /**
     * Creates a new LCDDisplay component with default values
     *
     * @param pi4j Pi4J context
     */
    public LcdDisplay(Context pi4j){
        this(pi4j, 2, 16, DEFAULT_DEVICE);
    }

    /**
     * Creates a new LCDDisplay component with custom rows and columns
     *
     * @param pi4j      Pi4J context
     * @param rows      Custom amount of display lines
     * @param columns   Custom amount of chars on line
     */
    public LcdDisplay(Context pi4j, int rows, int columns){
        this(pi4j, rows, columns, DEFAULT_DEVICE);
    }

    /**
     * Creates a new LCDDisplay component with custom rows and columns
     *
     * @param pi4j      Pi4J context
     * @param rows      Custom amount of display lines
     * @param columns   Custom amount of chars on line
     * @param device    Custom I2C device Address
     */
    public LcdDisplay(Context pi4j, int rows, int columns, int device) {
        super(pi4j, device, "PCF8574AT backed LCD@");
        this.rows = rows;
        this.columns = columns;
    }


    /**
     * Initializes the LCD with the backlight off
     */
    @Override
    protected void init(I2C i2c) {
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
    }

    /**
     * Turns the backlight on or off
     */
    public void setDisplayBacklight(boolean state) {
        backlight = state;
        executeCommand(backlight ? LCD_BACKLIGHT : LCD_NO_BACKLIGHT);
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
        delay(Duration.ofMillis(3));
    }

    /**
     * Shuts the display off
     */
    public void off() {
        executeCommand(LCD_DISPLAY_OFF);
    }

    /**
     * Write a Line of Text on the LCD Display
     *
     * @param text Text to display
     * @param line Select Line of Display
     */
    public void displayText(String text, int line) {
        if (text.length() > columns) {
            throw new IllegalArgumentException("Too long text. Only " + columns + " characters possible");
        }
        if (line > rows || line < 1) {
            throw new IllegalArgumentException("Wrong line id. Only " + rows + " lines possible");
        }

        clearLine(line);
        moveCursorHome();
        displayLine(text, LCD_ROW_OFFSETS[line - 1]);
    }

    /**
     * Write a Line of Text on the LCD Display
     *
     * @param text     Text to display
     * @param line     Select Line of Display
     * @param position Select position on line
     */
    public void displayText(String text, int line, int position) {
        if (position > columns) {
            throw new IllegalArgumentException("Too long text. Only " + columns + " characters possible");
        }
        if (line > rows || line < 1) {
            throw new IllegalArgumentException("Wrong line id. Only " + rows + " lines possible");
        }

        clearLine(line);
        setCursorToPosition(position, line);
        for (char character: text.toCharArray()) {
            writeCharacter(character);
        }
    }

    /**
     * Write Text on the LCD Display
     *
     * @param text Text to display
     */
    public void displayText(String text) {
        if (text.length() > (rows * columns)) {
            throw new IllegalArgumentException("Too long text. Only " + rows * columns + " characters allowed");
        }

        // Clean and prepare to write some text
        var currentLine = 0;
        String[] lines = new String[rows];
        for (int j = 0; j < rows; j++) {
            lines[j] = "";
        }
        clearDisplay();

        // Iterate through lines and characters and write them to the display
        for (int i = 0; i < text.length(); i++) {
            // line break in text found
            if (text.charAt(i) == '\n' && currentLine < rows) {
                currentLine++;
                //if a space comes after newLine, it is omitted
                if (i+1 >= text.length()) return;
                if (text.charAt(i + 1) == ' ') i++;
                continue;
            }

            // Write character to array
            lines[currentLine] += (char) text.charAt(i);

            if (lines[currentLine].length() == columns && currentLine < rows) {
                currentLine++;
                //if a space comes after newLine, it is omitted
                if (i+1 >= text.length()) return;
                if (text.charAt(i + 1) == ' ') i++;
            }
        }

        //display the created Rows
        for (int j = 0; j < rows; j++) {
            displayLine(lines[j], LCD_ROW_OFFSETS[j]);
        }
    }

    /**
     * write a character to lcd
     *
     * @param charvalue of the char that is written
     */
    public void writeCharacter(char charvalue) {
        writeSplitCommand((byte) charvalue, Rs);
    }

    /**
     * write a character to lcd
     *
     * @param charvalue of the char that is written
     * @param line      ROW-position, Range 1 - ROWS
     * @param column    Column-position, Range 0 - COLUMNS-1
     */
    public void writeCharacter(char charvalue, int column, int line) {
        setCursorToPosition(column, line);
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

        if (text == null || text.isEmpty()) return;
        for (int i = 0; i < text.length(); i++) {
            writeCharacter(text.charAt(i));
        }
    }

    /**
     * Clears a line of the display
     *
     * @param line Select line to clear
     */
    public void clearLine(int line) {
        if (line > rows || line < 1) {
            throw new IllegalArgumentException("Wrong line id. Only " + rows + " lines possible");
        }
        displayLine(" ".repeat(columns), LCD_ROW_OFFSETS[line - 1]);
    }


    /**
     * Clocks EN to latch command
     */
    private void lcd_strobe(byte data) {
        byte backlight = this.backlight ? LCD_BACKLIGHT : LCD_NO_BACKLIGHT;
        write((byte) (data | En | backlight));
        delay(Duration.ofNanos(500_000));
        write((byte) ((data & ~En) | backlight));
        delay(Duration.ofNanos(100_000));
    }

    /**
     * Moves the cursor 1 character right
     */
    public void moveCursorRight() {
        executeCommand(LCD_CURSOR_SHIFT, (byte) (LCD_CURSOR_MOVE | LCD_MOVE_RIGHT));
        delay(Duration.ofMillis(1));
    }

    /**
     * Moves the cursor 1 character left
     */
    public void moveCursorLeft() {
        executeCommand(LCD_CURSOR_SHIFT, (byte) (LCD_CURSOR_MOVE | LCD_MOVE_LEFT));
        delay(Duration.ofMillis(1));
    }

    /**
     * Sets the cursor to a target destination
     *
     * @param digit Selects the character of the line. Range: 0 - Columns-1
     * @param line  Selects the line of the display. Range: 1 - ROWS
     */
    public void setCursorToPosition(int digit, int line) {
        if (line > rows || line < 1) {
            throw new IllegalArgumentException("Line out of range. Display has only " + rows + "x" + columns + " Characters!");
        }

        if (digit < 0 || digit > columns) {
            throw new IllegalArgumentException("Line out of range. Display has only " + rows + "x" + columns + " Characters!");
        }
        writeCommand((byte) (LCD_SET_DDRAM_ADDR | digit + LCD_ROW_OFFSETS[line - 1]));
    }

    /**
     * Set the cursor to desired line
     *
     * @param line Sets the cursor to this line. Only Range 1 - lines allowed.
     */
    public void setCursorToLine(int line) {
        setCursorToPosition(0, line);
    }

    /**
     * Sets the display cursor to hidden or showing
     *
     * @param show Set the state of the cursor
     */
    public void setCursorVisibility(boolean show) {
        if (show) {
            this.displayControl |= LCD_CURSOR_ON;
        } else {
            this.displayControl &= ~LCD_CURSOR_ON;
        }
        executeCommand(LCD_DISPLAY_CONTROL, this.displayControl);
    }

    /**
     * Set the cursor to blinking or static
     *
     * @param blink Blink = true means the cursor will change to blinking mode. False lets the cursor stay static
     */
    public void setCursorBlinking(boolean blink) {
        if (blink) {
            this.displayControl |= LCD_BLINK_ON;
        } else {
            this.displayControl &= ~LCD_BLINK_ON;
        }

        executeCommand(LCD_DISPLAY_CONTROL, this.displayControl);
    }

    /**
     * Moves the whole displayed text one character right
     */
    public void moveDisplayRight() {
        executeCommand(LCD_CURSOR_SHIFT, (byte) (LCD_DISPLAY_MOVE | LCD_MOVE_RIGHT));
    }

    /**
     * Moves the whole displayed text one character right
     */
    public void moveDisplayLeft() {
        executeCommand(LCD_CURSOR_SHIFT, (byte) (LCD_DISPLAY_MOVE | LCD_MOVE_LEFT));
    }

    /**
     * Create a custom character by providing the single digit states of each pixel. Simply pass an Array of bytes
     * which will be translated to a character.
     *
     * @param location  Set the memory location of the character. 1 - 7 is possible.
     * @param character Byte array representing the pixels of a character
     */
    public void createCharacter(int location, byte[] character) {
        if (character.length != 8) {
            throw new IllegalArgumentException("Array has invalid length. Character is only 5x8 Digits. Only a array with length" +
                    " 8 is allowed");
        }

        if (location > 7 || location < 1) {
            throw new IllegalArgumentException("Invalid memory location. Range 1-7 allowed. Value: " + location);
        }
        writeCommand((byte) (LCD_SET_CGRAM_ADDR | location << 3));

        for (int i = 0; i < 8; i++) {
            writeSplitCommand(character[i], (byte) 1);
        }
    }
    /**
     * Write a command to the LCD
     */
    private void writeCommand(byte cmd) {
        writeSplitCommand(cmd, (byte) 0);
    }

    /**
     * Write a command in 2 parts to the LCD
     */
    private void writeSplitCommand(byte cmd, byte mode) {
        //bitwise AND with 11110000 to remove last 4 bits
        writeFourBits((byte) (mode | (cmd & 0xF0)));
        //bitshift and bitwise AND to remove first 4 bits
        writeFourBits((byte) (mode | ((cmd << 4) & 0xF0)));
    }

    /**
     * Write the four bits of a byte to the LCD
     *
     * @param data the byte that is sent
     */
    private void writeFourBits(byte data) {
        write((byte) (data | (backlight ? LCD_BACKLIGHT : LCD_NO_BACKLIGHT)));
        lcd_strobe(data);
    }




}
