package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import com.pi4j.plugin.mock.provider.i2c.MockI2C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LCDDisplayTest extends ComponentTest{
    LCDDisplay lcd420Display;
    LCDDisplay lcd216Display;
    MockI2C i2c;

    @BeforeEach
    public void setUp(){
        lcd420Display = new LCDDisplay(pi4j, 4, 20);
        lcd216Display = new LCDDisplay(pi4j, 2, 16, 0x2, 0x28);
        i2c = toMock(lcd420Display.getI2C());
    }


    @Test
    void testWriteTooLongText() {
        // when
        String text = "too long text to write the 2x16 display";

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            lcd216Display.displayText(text);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd216Display.displayText(text, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd216Display.displayText(text, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd216Display.displayText(text, 2);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd216Display.displayText(text, 3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd420Display.displayText(text, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd420Display.displayText(text, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd420Display.displayText(text, 2);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd420Display.displayText(text, 3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd420Display.displayText(text, 4);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            lcd420Display.displayText(text, 5);
        });
    }

    @Test
    void testWriteTextWithAllowedLength() {
        // when
        String text = "This is ok";

        // then
        assertDoesNotThrow(() -> {
            lcd216Display.displayText(text, 1);
            lcd216Display.displayText(text, 2);
            lcd216Display.displayText(text);
        });

    }

    @Test
    void testClearInvalidLine() {
        // then
        assertThrows(IllegalArgumentException.class, () -> {
            lcd216Display.clearLine(3);
        });
    }
}
