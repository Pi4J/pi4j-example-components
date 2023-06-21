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

public class JoystickComponentTest5_Inputs extends ComponentTest {

    private Joystick joystick;
    private MockDigitalInput diNorth;
    private MockDigitalInput diEast;
    private MockDigitalInput diSouth;
    private MockDigitalInput diWest;
    private MockDigitalInput diPush;

    private final PIN pinNorth = PIN.D21;
    private final PIN pinEast = PIN.D22;
    private final PIN pinSouth = PIN.D23;
    private final PIN pinWest = PIN.D24;
    private final PIN pinPush = PIN.D25;

    @BeforeEach
    public void setUp() {
        this.joystick = new Joystick(pi4j, pinNorth, pinEast, pinSouth, pinWest, pinPush);
        diNorth = toMock(joystick.getDigitalInputButtonNorth());
        diEast = toMock(joystick.getDigitalInputButtonEast());
        diSouth = toMock(joystick.getDigitalInputButtonSouth());
        diWest = toMock(joystick.getDigitalInputButtonWest());
        diPush = toMock(joystick.getDigitalInputButtonPush());
    }

    @ParameterizedTest
    @CsvSource({
            "LOW,LOW,LOW, LOW, LOW",
            "LOW,LOW,LOW, LOW, HIGH",
            "LOW,LOW,LOW, HIGH, LOW",
            "LOW,LOW,LOW, HIGH, HIGH",
            "LOW,LOW,HIGH, LOW, LOW",
            "LOW,LOW,HIGH, LOW, HIGH",
            "LOW,LOW,HIGH, HIGH, LOW",
            "LOW,LOW,HIGH, HIGH, HIGH",
            "LOW,HIGH,LOW, LOW, LOW",
            "LOW,HIGH,LOW, LOW, HIGH",
            "LOW,HIGH,LOW, HIGH, LOW",
            "LOW,HIGH,LOW, HIGH, HIGH",
            "LOW,HIGH,HIGH, LOW, LOW",
            "LOW,HIGH,HIGH, LOW, HIGH",
            "LOW,HIGH,HIGH, HIGH, LOW",
            "LOW,HIGH,HIGH, HIGH, HIGH",
            "HIGH,LOW,LOW, LOW, LOW",
            "HIGH,LOW,LOW, LOW, HIGH",
            "HIGH,LOW,LOW, HIGH, LOW",
            "HIGH,LOW,LOW, HIGH, HIGH",
            "HIGH,LOW,HIGH, LOW, LOW",
            "HIGH,LOW,HIGH, LOW, HIGH",
            "HIGH,LOW,HIGH, HIGH, LOW",
            "HIGH,LOW,HIGH, HIGH, HIGH",
            "HIGH,HIGH,LOW, LOW, LOW",
            "HIGH,HIGH,LOW, LOW, HIGH",
            "HIGH,HIGH,LOW, HIGH, LOW",
            "HIGH,HIGH,LOW, HIGH, HIGH",
            "HIGH,HIGH,HIGH, LOW, LOW",
            "HIGH,HIGH,HIGH, LOW, HIGH",
            "HIGH,HIGH,HIGH, HIGH, LOW",
            "HIGH,HIGH,HIGH, HIGH, HIGH"
    })
    public void testGetStates(DigitalState digitalStateNorth, DigitalState digitalStateEast, DigitalState digitalStateSouth, DigitalState digitalStateWest, DigitalState digitalStatePush) {
        // when
        List<DigitalState> testList = new ArrayList<>();
        testList.add(digitalStateNorth);
        testList.add(digitalStateEast);
        testList.add(digitalStateSouth);
        testList.add(digitalStateWest);
        testList.add(digitalStatePush);

        diNorth.mockState(digitalStateNorth);
        diEast.mockState(digitalStateEast);
        diSouth.mockState(digitalStateSouth);
        diWest.mockState(digitalStateWest);
        diPush.mockState(digitalStatePush);

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
        assertEquals(DigitalState.LOW, joystick.getStatePush());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        assertEquals(DigitalState.LOW, joystick.getStatePush());
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
        assertEquals(DigitalState.LOW, joystick.getStatePush());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        assertEquals(DigitalState.LOW, joystick.getStatePush());
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
        assertEquals(DigitalState.LOW, joystick.getStatePush());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        assertEquals(DigitalState.LOW, joystick.getStatePush());
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
        assertEquals(DigitalState.LOW, joystick.getStatePush());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        assertEquals(DigitalState.LOW, joystick.getStatePush());
    }

    @Test
    public void testGetStatePush() {
        //when
        diPush.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        assertEquals(DigitalState.HIGH, joystick.getStatePush());
        //when
        diPush.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joystick.getStateNorth());
        assertEquals(DigitalState.LOW, joystick.getStateEast());
        assertEquals(DigitalState.LOW, joystick.getStateSouth());
        assertEquals(DigitalState.LOW, joystick.getStateWest());
        assertEquals(DigitalState.LOW, joystick.getStatePush());
    }

    @Test
    public void testButtonPushIsDown() {
        //when
        diPush.mockState(DigitalState.HIGH);
        //then
        assertTrue(joystick.buttonPushIsDown());
        //when
        diPush.mockState(DigitalState.LOW);
        //then
        assertFalse(joystick.buttonPushIsDown());
    }

    @Test
    public void testButtonPushIsUp() {
        //when
        diPush.mockState(DigitalState.HIGH);
        //then
        assertFalse(joystick.buttonPushIsUp());
        //when
        diPush.mockState(DigitalState.LOW);
        //then
        assertTrue(joystick.buttonPushIsUp());
    }

    @Test
    public void testOnPushDown(){
        //given
        int[] counter = {0};
        diPush.mockState(DigitalState.LOW);
        joystick.onPushDown(()->counter[0]++);

        //when
        diPush.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diPush.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diPush.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        diPush.mockState(DigitalState.HIGH);

        //then
        assertEquals(2, counter[0]);

        //when
        joystick.deRegisterAll();

        //then
        assertNull(joystick.getOnNorth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnSouth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnPush());
    }

    @Test
    public void testOnUp(){
        //given
        int[] counter = {0};
        diPush.mockState(DigitalState.LOW);
        joystick.onPushUp(()->counter[0]++);

        //when
        diPush.mockState(DigitalState.HIGH);

        //then
        assertEquals(0, counter[0]);

        //when
        diPush.mockState(DigitalState.LOW);

        //then
        assertEquals(1, counter[0]);

        //when
        diPush.mockState(DigitalState.HIGH);

        //then
        assertEquals(1, counter[0]);

        //when
        diPush.mockState(DigitalState.LOW);

        //then
        assertEquals(2, counter[0]);

        //when
        joystick.deRegisterAll();

        //then
        assertNull(joystick.getOnNorth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnSouth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnPush());

    }

    @Test
    public void testWhilePressed() throws InterruptedException {
        //given
        int samplingTime = 100;


        int[] counter = {0};

        joystick.pushWhilePushed(samplingTime, () -> counter[0]++);

        //when
        diPush.mockState(DigitalState.HIGH);

        //when
        sleep(2 * samplingTime);

        //stop whilePressed
        diPush.mockState(DigitalState.LOW);

        int currentCount = counter[0];
        assertTrue(currentCount <= 2);

        //when
        sleep(2 * samplingTime);

        //then
        assertEquals(currentCount, counter[0]);

        //when
        joystick.deRegisterAll();
        sleep(100);

        //then
        assertNull(joystick.getOnNorth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnSouth());
        assertNull(joystick.getOnEast());
        assertNull(joystick.getOnPush());
    }

    @Test
    public void testGetDigitalInputButtonPush() {
        assertEquals(diPush, joystick.getDigitalInputButtonPush());
    }
}
