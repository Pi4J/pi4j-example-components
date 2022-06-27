package com.pi4j.components.components;

import com.pi4j.components.ComponentTest;
import com.pi4j.components.components.helpers.PIN;
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

    private Joystick joysitck;
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
        this.joysitck = new Joystick(pi4j, pinNorth, pinEast, pinSouth, pinWest);
        diNorth = toMock(joysitck.getDigitalInputButtonNorth());
        diEast = toMock(joysitck.getDigitalInputButtonEast());
        diSouth = toMock(joysitck.getDigitalInputButtonSouth());
        diWest = toMock(joysitck.getDigitalInputButtonWest());
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
        assertEquals(testList, joysitck.getStates());
    }

    @Test
    public void testGetStateNorth() {
        //when
        diNorth.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.HIGH, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
    }

    @Test
    public void testGetStateEast() {
        //when
        diEast.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.HIGH, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
    }

    @Test
    public void testGetStateSouth() {
        //when
        diSouth.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.HIGH, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
    }

    @Test
    public void testGetStateWest() {
        //when
        diWest.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.HIGH, joysitck.getStateWest());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
    }

    @Test
    public void testGetStatePush() {
        assertEquals(DigitalState.UNKNOWN, joysitck.getStatePush());
    }

    @Test
    public void testButtonNorthIsDown() {
        //when
        diNorth.mockState(DigitalState.HIGH);
        //then
        assertTrue(joysitck.buttonNorthIsDown());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertFalse(joysitck.buttonNorthIsDown());
    }

    @Test
    public void testButtonNorthIsUp() {
        //when
        diNorth.mockState(DigitalState.HIGH);
        //then
        assertFalse(joysitck.buttonNorthIsUp());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertTrue(joysitck.buttonNorthIsUp());
    }

    @Test
    public void testButtonEastIsDown() {
        //when
        diEast.mockState(DigitalState.HIGH);
        //then
        assertTrue(joysitck.buttonEastIsDown());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertFalse(joysitck.buttonEastIsDown());
    }

    @Test
    public void testButtonEastIsUp() {
        //when
        diEast.mockState(DigitalState.HIGH);
        //then
        assertFalse(joysitck.buttonEastIsUp());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertTrue(joysitck.buttonEastIsUp());
    }

    @Test
    public void testButtonSouthIsDown() {
        //when
        diSouth.mockState(DigitalState.HIGH);
        //then
        assertTrue(joysitck.buttonSouthIsDown());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertFalse(joysitck.buttonSouthIsDown());
    }

    @Test
    public void testButtonSouthIsUp() {
        //when
        diSouth.mockState(DigitalState.HIGH);
        //then
        assertFalse(joysitck.buttonSouthIsUp());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertTrue(joysitck.buttonSouthIsUp());
    }

    @Test
    public void testButtonWestIsDown() {
        //when
        diWest.mockState(DigitalState.HIGH);
        //then
        assertTrue(joysitck.buttonWestIsDown());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertFalse(joysitck.buttonWestIsDown());
    }

    @Test
    public void testButtonWestIsUp() {
        //when
        diWest.mockState(DigitalState.HIGH);
        //then
        assertFalse(joysitck.buttonWestIsUp());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertTrue(joysitck.buttonWestIsUp());
    }

    @Test
    public void testButtonPushIsDown() {
        assertFalse(joysitck.buttonPushIsDown());
    }

    @Test
    public void testButtonPushIsUp() {
        assertFalse(joysitck.buttonPushIsUp());
    }

    @Test
    public void testOnNorth() {
        //given
        int[] counter = {0};
        diNorth.mockState(DigitalState.LOW);
        joysitck.onNorth(() -> counter[0]++);

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
        joysitck.deRegisterAll();

        //then
        assertNull(joysitck.getOnNorth());
        assertNull(joysitck.getOnEast());
        assertNull(joysitck.getOnSouth());
        assertNull(joysitck.getOnWest());
        assertNull(joysitck.getOnPush());
    }

    @Test
    public void testWhileNorth() throws InterruptedException {
        //given
        int samplingTime = 100;

        int[] counter = {0};

        joysitck.whileNorth(samplingTime, () -> counter[0]++);

        //when
        diNorth.mockState(DigitalState.HIGH);

        //then
        assertEquals(0, counter[0]);

        //when
        sleep(150);

        //then
        assertEquals(1, counter[0]);

        //when
        sleep(100);

        //then
        assertEquals(2, counter[0]);

        //when
        joysitck.deRegisterAll();
        sleep(100);

        //then
        assertEquals(2, counter[0]);
        assertNull(joysitck.getWhileNorth());
        assertNull(joysitck.getWhileEast());
        assertNull(joysitck.getWhileSouth());
        assertNull(joysitck.getWhileWest());
        assertNull(joysitck.getWhilePush());

    }

    @Test
    public void testOnEast() {
        //given
        int[] counter = {0};
        diEast.mockState(DigitalState.LOW);
        joysitck.onEast(() -> counter[0]++);

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
        joysitck.deRegisterAll();

        //then
        assertNull(joysitck.getOnNorth());
        assertNull(joysitck.getOnEast());
        assertNull(joysitck.getOnSouth());
        assertNull(joysitck.getOnWest());
        assertNull(joysitck.getOnPush());
    }

    @Test
    public void testWhileEast() throws InterruptedException {
        //given
        int samplingTime = 100;

        int[] counter = {0};

        joysitck.whileEast(samplingTime, () -> counter[0]++);

        //when
        diEast.mockState(DigitalState.HIGH);

        //then
        assertEquals(0, counter[0]);

        //when
        sleep(150);

        //then
        assertEquals(1, counter[0]);

        //when
        sleep(100);

        //then
        assertEquals(2, counter[0]);

        //when
        joysitck.deRegisterAll();
        sleep(100);

        //then
        assertEquals(2, counter[0]);
        assertNull(joysitck.getWhileNorth());
        assertNull(joysitck.getWhileEast());
        assertNull(joysitck.getWhileSouth());
        assertNull(joysitck.getWhileWest());
        assertNull(joysitck.getWhilePush());

    }

    @Test
    public void testOnSouth() {
        //given
        int[] counter = {0};
        diSouth.mockState(DigitalState.LOW);
        joysitck.onSouth(() -> counter[0]++);

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
        joysitck.deRegisterAll();

        //then
        assertNull(joysitck.getOnNorth());
        assertNull(joysitck.getOnEast());
        assertNull(joysitck.getOnSouth());
        assertNull(joysitck.getOnWest());
        assertNull(joysitck.getOnPush());
    }

    @Test
    public void testWhileSouth() throws InterruptedException {
        //given
        int samplingTime = 100;

        int[] counter = {0};

        joysitck.whileSouth(samplingTime, () -> counter[0]++);

        //when
        diSouth.mockState(DigitalState.HIGH);

        //then
        assertEquals(0, counter[0]);

        //when
        sleep(150);

        //then
        assertEquals(1, counter[0]);

        //when
        sleep(100);

        //then
        assertEquals(2, counter[0]);

        //when
        joysitck.deRegisterAll();
        sleep(100);

        //then
        assertEquals(2, counter[0]);
        assertNull(joysitck.getWhileNorth());
        assertNull(joysitck.getWhileEast());
        assertNull(joysitck.getWhileSouth());
        assertNull(joysitck.getWhileWest());
        assertNull(joysitck.getWhilePush());

    }

    @Test
    public void testOnWest() {
        //given
        int[] counter = {0};
        diWest.mockState(DigitalState.LOW);
        joysitck.onWest(() -> counter[0]++);

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
        joysitck.deRegisterAll();

        //then
        assertNull(joysitck.getOnNorth());
        assertNull(joysitck.getOnEast());
        assertNull(joysitck.getOnSouth());
        assertNull(joysitck.getOnWest());
        assertNull(joysitck.getOnPush());
    }

    @Test
    public void testWhileWest() throws InterruptedException {
        //given
        int samplingTime = 100;

        int[] counter = {0};

        joysitck.whileWest(samplingTime, () -> counter[0]++);

        //when
        diWest.mockState(DigitalState.HIGH);

        //then
        assertEquals(0, counter[0]);

        //when
        sleep(150);

        //then
        assertEquals(1, counter[0]);

        //when
        sleep(100);

        //then
        assertEquals(2, counter[0]);

        //when
        joysitck.deRegisterAll();
        sleep(100);

        //then
        assertEquals(2, counter[0]);
        assertNull(joysitck.getWhileNorth());
        assertNull(joysitck.getWhileEast());
        assertNull(joysitck.getWhileSouth());
        assertNull(joysitck.getWhileWest());
        assertNull(joysitck.getWhilePush());

    }

    @Test
    public void testGetDigitalInputButtonNorth() {
        assertEquals(diNorth, joysitck.getDigitalInputButtonNorth());
    }

    @Test
    public void testGetDigitalInputButtonEast() {
        assertEquals(diEast, joysitck.getDigitalInputButtonEast());
    }

    @Test
    public void testGetDigitalInputButtonSouth() {
        assertEquals(diSouth, joysitck.getDigitalInputButtonSouth());
    }

    @Test
    public void testGetDigitalInputButtonWest() {
        assertEquals(diWest, joysitck.getDigitalInputButtonWest());
    }

    @Test
    public void testGetDigitalInputButtonPush() {
        assertNull(joysitck.getDigitalInputButtonPush());
    }


}
