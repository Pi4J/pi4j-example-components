package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutput;

import com.pi4j.catalog.ComponentTest;
import com.pi4j.catalog.components.base.PIN;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
A LedButton consists of a SimpleButton and a SimpleLed.

There is no need to retest the whole button- and LED-functionality (this is tested in SimpleButtonTest and SimpleLedTest, respectively).

We just need to test the correct "wiring".
 */
public class LedButtonTest extends ComponentTest {

    private final PIN btnPinNumber = PIN.D26;
    private final PIN ledPinNumber = PIN.D25;

    private LedButton button;

    private MockDigitalInput digitalInput;
    private MockDigitalOutput digitalOutput;

    @BeforeEach
    public void setUp() {
        button = new LedButton(pi4j, btnPinNumber, false, ledPinNumber);
        digitalInput  = button.mockButton();
        digitalOutput = button.mockLed();
    }

    @Test
    public void testButtonWiring(){
        //given
        Counter counter = new Counter();
        button.onDown(counter::increase);

        //when
        digitalInput.mockState(DigitalState.HIGH);
        //then
        assertTrue(button.isDown());
        assertEquals(1, counter.count);

        //when
        digitalInput.mockState(DigitalState.LOW);
        //then
        assertTrue(button.isUp());
    }


    @Test
    public void testLedWiring(){
        //when
        button.ledOn();
        //then
        assertEquals(DigitalState.HIGH, digitalOutput.state());

        //when
        button.ledOff();
        //then
        assertEquals(DigitalState.LOW, digitalOutput.state());
    }


    private class Counter {
        int count;

        void increase(){
            count++;
        }
    }
}