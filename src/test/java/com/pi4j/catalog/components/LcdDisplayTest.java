package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.plugin.mock.provider.i2c.MockI2C;

import com.pi4j.catalog.ComponentTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LcdDisplayTest extends ComponentTest {
    LcdDisplay lcd420Display;
    LcdDisplay lcd216Display;
    MockI2C i2c;

    @BeforeEach
    public void setUp() {
        lcd420Display = new LcdDisplay(pi4j, 4, 20);
        lcd216Display = new LcdDisplay(pi4j, 2, 16, 0x2, 0x28);
        i2c = lcd420Display.mock();
    }

    @Test
    public void testWriteTooLongText() {
        // when
        String text = "too long text to write the 2x16 display";

        // then
        assertThrows(IllegalArgumentException.class, () -> lcd216Display.displayText(text));

        for (int i : new int[]{0, 1, 2, 3}) {
            assertThrows(IllegalArgumentException.class, () -> lcd216Display.displayText(text, i));
        }

        for (int i : new int[]{0, 1, 2, 3, 4, 5}) {
            assertThrows(IllegalArgumentException.class, () -> lcd420Display.displayText(text, i));
        }

    }

    @Test
    public void testWriteTextWithAllowedLength() {
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
    public void testClearInvalidLine() {
        // then
        assertThrows(IllegalArgumentException.class, () -> lcd216Display.clearLine(3));
    }
}
