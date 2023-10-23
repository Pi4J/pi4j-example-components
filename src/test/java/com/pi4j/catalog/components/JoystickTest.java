package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;

import com.pi4j.catalog.ComponentTest;
import com.pi4j.catalog.components.base.PIN;

import static org.junit.jupiter.api.Assertions.*;

/*
A digital joystick consists of 4 or 5 SimpleButtons (depending on whether a Push-Button is available).

It's sufficient to test whether the "wiring" of these buttons is OK. There's no need to retest the functionality of a single button again.
 */
public class JoystickTest extends ComponentTest {

    private Joystick joystick;

    private MockDigitalInput diNorth;
    private MockDigitalInput diEast;
    private MockDigitalInput diSouth;
    private MockDigitalInput diWest;

    private final PIN pinNorth = PIN.D21;
    private final PIN pinEast  = PIN.D22;
    private final PIN pinSouth = PIN.D23;
    private final PIN pinWest  = PIN.D24;

    @BeforeEach
    public void setUp() {
        joystick = new Joystick(pi4j, pinNorth, pinEast, pinSouth, pinWest);
        diNorth  = joystick.mockNorth();
        diEast   = joystick.mockEast();
        diSouth  = joystick.mockSouth();
        diWest   = joystick.mockWest();
    }

    @Test
    public void testInitialization(){
        assertTrue(joystick.isInInitialState());
    }

    @Test
    public void testDeRegisterAll(){
        //given
        Runnable task = () -> System.out.println("not important for test");

        joystick.onEast(task);
        joystick.onWest(task);
        joystick.onNorth(task);
        joystick.onSouth(task);

        //when
        joystick.reset();

        //then
        assertTrue(joystick.isInInitialState());
    }

    @Test
    public void testButtonNorth() {
        //when
        diNorth.mockState(DigitalState.HIGH);

        //then
        assertTrue(joystick.isNorth());

        //when
        diNorth.mockState(DigitalState.LOW);

        //then
        assertFalse(joystick.isNorth());
    }

    @Test
    public void testButtonEast() {
        //when
        diEast.mockState(DigitalState.HIGH);

        //then
        assertTrue(joystick.isEast());

        //when
        diEast.mockState(DigitalState.LOW);

        //then
        assertFalse(joystick.isEast());
    }

    @Test
    public void testButtonSouth() {
        //when
        diSouth.mockState(DigitalState.HIGH);

        //then
        assertTrue(joystick.isSouth());

        //when
        diSouth.mockState(DigitalState.LOW);

        //then
        assertFalse(joystick.isSouth());
    }


    @Test
    public void testButtonWest() {
        //when
        diWest.mockState(DigitalState.HIGH);

        //then
        assertTrue(joystick.isWest());

        //when
        diWest.mockState(DigitalState.LOW);

        //then
        assertFalse(joystick.isWest());
    }


    @Test
    public void testButtonPush() {
        assertFalse(joystick.isPushed());
    }

    @Test
    public void testJoystickWithButton(){
        //given
        joystick = new Joystick(pi4j, PIN.D5, PIN.D6, PIN.D17, PIN.D16, PIN.D11);

        MockDigitalInput diButton = joystick.mockPush();

        //when
        diButton.mockState(DigitalState.HIGH);

        //then
        assertTrue(joystick.isPushed());

        //when
        diButton.mockState(DigitalState.LOW);

        //then
        assertFalse(joystick.isPushed());
    }

    @Test
    public void testGambling(){
        //given
        Counter northCounter = new Counter();
        Counter southCounter = new Counter();
        Counter eastCounter  = new Counter();
        Counter westCounter  = new Counter();
        Counter centerCounter = new Counter();

        joystick.onNorth(northCounter::increase);
        joystick.onSouth(southCounter::increase);
        joystick.onEast(eastCounter::increase);
        joystick.onWest(westCounter::increase);
        joystick.onCenter(centerCounter::increase);

        //when
        diWest.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(0, eastCounter.count);
        assertEquals(0, northCounter.count);
        assertEquals(0, southCounter.count);
        assertEquals(0, centerCounter.count);

        //when
        diWest.mockState(DigitalState.LOW);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(0, eastCounter.count);
        assertEquals(0, northCounter.count);
        assertEquals(0, southCounter.count);
        assertEquals(1, centerCounter.count);

        //when
        diEast.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(0, northCounter.count);
        assertEquals(0, southCounter.count);
        assertEquals(1, centerCounter.count);

        //when
        diEast.mockState(DigitalState.LOW);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(0, northCounter.count);
        assertEquals(0, southCounter.count);
        assertEquals(2, centerCounter.count);

        //when
        diNorth.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(1, northCounter.count);
        assertEquals(0, southCounter.count);
        assertEquals(2, centerCounter.count);

        //when
        diNorth.mockState(DigitalState.LOW);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(1, northCounter.count);
        assertEquals(0, southCounter.count);
        assertEquals(3, centerCounter.count);

        //when
        diSouth.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(1, northCounter.count);
        assertEquals(1, southCounter.count);
        assertEquals(3, centerCounter.count);

        //when
        diSouth.mockState(DigitalState.LOW);

        //then
        assertEquals(1, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(1, northCounter.count);
        assertEquals(1, southCounter.count);
        assertEquals(4, centerCounter.count);

        //when
        diSouth.mockState(DigitalState.HIGH);
        diWest.mockState(DigitalState.HIGH);

        //then
        assertEquals(2, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(1, northCounter.count);
        assertEquals(2, southCounter.count);
        assertEquals(4, centerCounter.count);

        //when
        diSouth.mockState(DigitalState.LOW);
        diWest.mockState(DigitalState.LOW);

        //then
        assertEquals(2, westCounter.count);
        assertEquals(1, eastCounter.count);
        assertEquals(1, northCounter.count);
        assertEquals(2, southCounter.count);
        assertEquals(6, centerCounter.count);
    }

    private class Counter {
        int count;

        void increase(){
            count++;
        }
    }

}
