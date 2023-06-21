package com.pi4j.catalog.components;

import com.pi4j.catalog.ComponentTest;
import com.pi4j.catalog.components.helpers.PIN;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class JoystickTest4_Inputs extends ComponentTest {

    private Joystick joystick;
    private MockDigitalInput diNorth;
    private MockDigitalInput diEast;
    private MockDigitalInput diSouth;
    private MockDigitalInput diWest;

    private final PIN pinNorth = PIN.D21;
    private final PIN pinEast = PIN.D22;
    private final PIN pinSouth = PIN.D23;
    private final PIN pinWest = PIN.D24;

    @BeforeEach
    public void setUp() {
        this.joystick = new Joystick(pi4j, pinNorth, pinEast, pinSouth, pinWest);
        diNorth = toMock(joystick.getDigitalInputButtonNorth());
        diEast = toMock(joystick.getDigitalInputButtonEast());
        diSouth = toMock(joystick.getDigitalInputButtonSouth());
        diWest = toMock(joystick.getDigitalInputButtonWest());
    }

    @ParameterizedTest
    @CsvSource({"LOW,LOW, LOW, LOW", "LOW,LOW, LOW, HIGH", "LOW,LOW, HIGH, LOW", "LOW,LOW, HIGH, HIGH", "LOW,HIGH, LOW, LOW", "LOW,HIGH, LOW, HIGH", "LOW,HIGH, HIGH, LOW", "LOW,HIGH, HIGH, HIGH", "HIGH,LOW, LOW, LOW", "HIGH,LOW, LOW, HIGH", "HIGH,LOW, HIGH, LOW", "HIGH,LOW, HIGH, HIGH", "HIGH,HIGH, LOW, LOW", "HIGH,HIGH, LOW, HIGH", "HIGH,HIGH, HIGH, LOW", "HIGH,HIGH, HIGH, HIGH"})
    public void testGetStates(DigitalState digitalStateNorth, DigitalState digitalStateEast, DigitalState digitalStateSouth, DigitalState digitalStateWest) {
        // when
        List<DigitalState> testList = new ArrayList<>();
        testList.add(digitalStateNorth);
        testList.add(digitalStateEast);
        testList.add(digitalStateSouth);
        testList.add(digitalStateWest);

        diNorth.mockState(digitalStateNorth);
        diEast.mockState(digitalStateEast);
        diSouth.mockState(digitalStateSouth);
        diWest.mockState(digitalStateWest);

        // then
        assertEquals(testList, joystick.getStates());
    }

    @Test
    public void testGetStateNorth() {
        //when
        diNorth.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.HIGH, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
    }

    @Test
    public void testGetStateEast() {
        //when
        diEast.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.HIGH, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
    }

    @Test
    public void testGetStateSouth() {
        //when
        diSouth.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.HIGH, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
    }

    @Test
    public void testGetStateWest() {
        //when
        diWest.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.HIGH, joystick.getStateWest());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
    }

    @Test
    public void testGetStatePush() {
        assertEquals(DigitalState.UNKNOWN, joystick.getStatePush());
    }

    @Test
    public void testButtonNorthIsDown() {
        //when
        diNorth.mockState(DigitalState.HIGH);
        //then
        assertTrue(joystick.buttonNorthIsDown());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertFalse(joystick.buttonNorthIsDown());
    }

    @Test
    public void testButtonNorthIsUp() {
        //when
        diNorth.mockState(DigitalState.HIGH);
        //then
        assertFalse(joystick.buttonNorthIsUp());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertTrue(joystick.buttonNorthIsUp());
    }

    @Test
    public void testButtonEastIsDown() {
        //when
        diEast.mockState(DigitalState.HIGH);
        //then
        assertTrue(joystick.buttonEastIsDown());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertFalse(joystick.buttonEastIsDown());
    }

    @Test
    public void testButtonEastIsUp() {
        //when
        diEast.mockState(DigitalState.HIGH);
        //then
        assertFalse(joystick.buttonEastIsUp());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertTrue(joystick.buttonEastIsUp());
    }

    @Test
    public void testButtonSouthIsDown() {
        //when
        diSouth.mockState(DigitalState.HIGH);
        //then
        assertTrue(joystick.buttonSouthIsDown());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertFalse(joystick.buttonSouthIsDown());
    }

    @Test
    public void testButtonSouthIsUp() {
        //when
        diSouth.mockState(DigitalState.HIGH);
        //then
        assertFalse(joystick.buttonSouthIsUp());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertTrue(joystick.buttonSouthIsUp());
    }

    @Test
    public void testButtonWestIsDown() {
        //when
        diWest.mockState(DigitalState.HIGH);
        //then
        assertTrue(joystick.buttonWestIsDown());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertFalse(joystick.buttonWestIsDown());
    }

    @Test
    public void testButtonWestIsUp() {
        //when
        diWest.mockState(DigitalState.HIGH);
        //then
        assertFalse(joystick.buttonWestIsUp());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertTrue(joystick.buttonWestIsUp());
    }

    @Test
    public void testButtonPushIsDown() {
        assertFalse(joystick.buttonPushIsDown());
    }

    @Test
    public void testButtonPushIsUp() {
        assertFalse(joystick.buttonPushIsUp());
    }

    @Test
    public void testOnNorth() {
        //given
        int[] counter = {0};
        diNorth.mockState(DigitalState.LOW);
        joystick.onNorth(() -> counter[0]++);

        //when
        diNorth.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diNorth.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diNorth.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        diNorth.mockState(DigitalState.HIGH);

        //then
        assertEquals(2, counter[0]);

        //when
        joystick.deRegisterAll();

        //then
        assertNull(joystick.getOnNorth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnSouth());
        assertNull(joystick.getOnWest());
        assertNull(joystick.getOnPush());
    }


    @Test
    public void testOnEast() {
        //given
        int[] counter = {0};
        diEast.mockState(DigitalState.LOW);
        joystick.onEast(() -> counter[0]++);

        //when
        diEast.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diEast.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diEast.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        diEast.mockState(DigitalState.HIGH);

        //then
        assertEquals(2, counter[0]);

        //when
        joystick.deRegisterAll();

        //then
        assertNull(joystick.getOnNorth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnSouth());
        assertNull(joystick.getOnWest());
        assertNull(joystick.getOnPush());
    }

    @Test
    public void testOnSouth() {
        //given
        int[] counter = {0};
        diSouth.mockState(DigitalState.LOW);
        joystick.onSouth(() -> counter[0]++);

        //when
        diSouth.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diSouth.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diSouth.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        diSouth.mockState(DigitalState.HIGH);

        //then
        assertEquals(2, counter[0]);

        //when
        joystick.deRegisterAll();

        //then
        assertNull(joystick.getOnNorth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnSouth());
        assertNull(joystick.getOnWest());
        assertNull(joystick.getOnPush());
    }


    @Test
    public void testOnWest() {
        //given
        int[] counter = {0};
        diWest.mockState(DigitalState.LOW);
        joystick.onWest(() -> counter[0]++);

        //when
        diWest.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diWest.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diWest.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        diWest.mockState(DigitalState.HIGH);

        //then
        assertEquals(2, counter[0]);

        //when
        joystick.deRegisterAll();

        //then
        assertNull(joystick.getOnNorth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnSouth());
        assertNull(joystick.getOnWest());
        assertNull(joystick.getOnPush());
    }

    @Test
    public void testGetDigitalInputButtonNorth() {
        assertEquals(diNorth, joystick.getDigitalInputButtonNorth());
    }

    @Test
    public void testGetDigitalInputButtonEast() {
        assertEquals(diEast, joystick.getDigitalInputButtonEast());
    }

    @Test
    public void testGetDigitalInputButtonSouth() {
        assertEquals(diSouth, joystick.getDigitalInputButtonSouth());
    }

    @Test
    public void testGetDigitalInputButtonWest() {
        assertEquals(diWest, joystick.getDigitalInputButtonWest());
    }

    @Test
    public void testGetDigitalInputButtonPush() {
        assertNull(joystick.getDigitalInputButtonPush());
    }


}
