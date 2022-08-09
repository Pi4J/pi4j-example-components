package com.pi4j.catalog.components;

import com.pi4j.catalog.ComponentTest;
import com.pi4j.catalog.components.helpers.PIN;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class LEDButtonTest extends ComponentTest {

    private LEDButton button;
    private MockDigitalInput digitalInput;
    private MockDigitalOutput digitalOutput;
    private final PIN BTNPinNumber = PIN.D26;
    private final PIN LEDPinNumber = PIN.D25;

    @BeforeEach
    public void setUp() {
        button = new LEDButton(pi4j, BTNPinNumber, false, LEDPinNumber);
        digitalInput = toMock(button.btngetDigitalInput());
        digitalOutput = toMock(button.LEDgetDigitalOutput());
    }

    @Test
    public void testGetState(){
        //when
        digitalInput.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.HIGH, button.btngetState());
        //when
        digitalInput.mockState(DigitalState.LOW);
        //the
        assertEquals(DigitalState.LOW, button.btngetState());
        //when
        digitalInput.mockState(DigitalState.UNKNOWN);
        //then
        assertEquals(DigitalState.UNKNOWN, button.btngetState());
    }

    @Test
    public void testIsDown(){
        //when
        digitalInput.mockState(DigitalState.HIGH);
        //then
        assertTrue(button.btnisDown());
        //when
        digitalInput.mockState(DigitalState.LOW);
        //then
        assertFalse(button.btnisDown());
    }

    @Test
    public void testIsUp(){
        //when
        digitalInput.mockState(DigitalState.HIGH);
        //then
        assertFalse(button.btnisUp());
        //when
        digitalInput.mockState(DigitalState.LOW);
        //then
        assertTrue(button.btnisUp());
    }

    @Test
    public void testGetDigitalInput(){
        assertEquals(BTNPinNumber.getPin(),button.btngetDigitalInput().address());
    }

    @Test
    public void testOnDown(){
        //given
        int[] counter = {0};
        digitalInput.mockState(DigitalState.LOW);
        button.onDown(()->counter[0]++);

        //when
        digitalInput.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        digitalInput.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        digitalInput.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        digitalInput.mockState(DigitalState.HIGH);

        //then
        assertEquals(2, counter[0]);

        //when
        button.btndeRegisterAll();

        //then
        assertNull(button.btnGetOnUp());
        assertNull(button.btnGetOnDown());
        assertNull(button.btnGetWhilePressed());
    }

    @Test
    public void testOnUp(){
        //given
        int[] counter = {0};
        digitalInput.mockState(DigitalState.LOW);
        button.onUp(()->counter[0]++);

        //when
        digitalInput.mockState(DigitalState.HIGH);

        //then
        assertEquals(0, counter[0]);

        //when
        digitalInput.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        digitalInput.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        digitalInput.mockState(DigitalState.LOW);

        //then
        assertEquals(2, counter[0]);

        //when
        button.btndeRegisterAll();

        //then
        assertNull(button.btnGetOnUp());
        assertNull(button.btnGetOnDown());
        assertNull(button.btnGetWhilePressed());

    }

    @Test
    public void testWhilePressed() throws InterruptedException {
        //given
        int samplingTime = 100;

        AtomicInteger counter = new AtomicInteger(0);

        button.btnwhilePressed(counter::getAndIncrement, samplingTime);

        //when
        digitalInput.mockState(DigitalState.HIGH);

        //then
        assertEquals(0, counter.get());

        //when
        sleep(150);

        //then
        assertEquals(1, counter.get());

        //when
        sleep(100);

        //then
        assertEquals(2, counter.get());

        //when
        button.btndeRegisterAll();
        sleep(100);

        //then
        assertEquals(2, counter.get());
        assertNull(button.btnGetOnUp());
        assertNull(button.btnGetOnDown());
        assertNull(button.btnGetWhilePressed());

        //cleanup
        digitalInput.mockState(DigitalState.LOW);

    }

    @Test
    public void testLED_Address(){
        //when
        digitalOutput = toMock(button.LEDgetDigitalOutput());
        //then
        assertEquals(LEDPinNumber.getPin(), digitalOutput.getAddress());

    }

    @Test
    public void testSetState_On(){
        //when
        button.LEDsetState(true);
        //then
        assertEquals(DigitalState.HIGH, digitalOutput.state());
    }

    @Test
    public void testSetState_Off(){
        //when
        button.LEDsetState(false);
        //then
        assertEquals(DigitalState.LOW, digitalOutput.state());
    }

    @Test
    public void testSetStateOn(){
        //when
        button.LEDsetStateOn();
        //then
        assertEquals(DigitalState.HIGH, digitalOutput.state());
    }

    @Test
    public void testSetStateOff(){
        //given
        button.LEDsetStateOn();
        //when
        button.LEDsetStateOff();
        //then
        assertEquals(DigitalState.LOW, digitalOutput.state());
    }

    @Test
    public void testToggleState(){
        //when
        button.LEDtoggleState();
        //then
        assertEquals(DigitalState.HIGH, digitalOutput.state());
    }
}