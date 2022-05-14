package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import com.pi4j.example.components.helpers.PIN;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Joystick_ComponentTest5_Inputs extends ComponentTest {

    private Joystick joysitck;
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
        this.joysitck = new Joystick(pi4j, pinNorth, pinEast, pinSouth, pinWest, pinPush);
        diNorth = toMock(joysitck.getDigitalInputButtonNorth());
        diEast = toMock(joysitck.getDigitalInputButtonEast());
        diSouth = toMock(joysitck.getDigitalInputButtonSouth());
        diWest = toMock(joysitck.getDigitalInputButtonWest());
        diPush = toMock(joysitck.getDigitalInputButtonPush());
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
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
        //when
        diNorth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
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
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
        //when
        diEast.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
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
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
        //when
        diSouth.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
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
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
        //when
        diWest.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
    }

    @Test
    public void testGetStatePush() {
        //when
        diPush.mockState(DigitalState.HIGH);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        assertEquals(DigitalState.HIGH, joysitck.getStatePush());
        //when
        diPush.mockState(DigitalState.LOW);
        //then
        assertEquals(DigitalState.LOW, joysitck.getStateNorth());
        assertEquals(DigitalState.LOW, joysitck.getStateEast());
        assertEquals(DigitalState.LOW, joysitck.getStateSouth());
        assertEquals(DigitalState.LOW, joysitck.getStateWest());
        assertEquals(DigitalState.LOW, joysitck.getStatePush());
    }

    @Test
    public void testButtonPushIsDown() {
        //when
        diPush.mockState(DigitalState.HIGH);
        //then
        assertEquals(true, joysitck.buttonPushIsDown());
        //when
        diPush.mockState(DigitalState.LOW);
        //then
        assertEquals(false, joysitck.buttonPushIsDown());
    }

    @Test
    public void testButtonPushIsUp() {
        //when
        diPush.mockState(DigitalState.HIGH);
        //then
        assertEquals(false, joysitck.buttonPushIsUp());
        //when
        diPush.mockState(DigitalState.LOW);
        //then
        assertEquals(true, joysitck.buttonPushIsUp());
    }

    public void testOnPushDown(){
        //given
        int[] counter = {0};
        diPush.mockState(DigitalState.LOW);
        joysitck.onPushDown(()->counter[0]++);

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
        joysitck.deRegisterAll();

        //then
        assertEquals(null, joysitck.getOnNorth());
        assertEquals(null, joysitck.getOnEast());
        assertEquals(null, joysitck.getOnSouth());
        assertEquals(null, joysitck.getOnEast());
        assertEquals(null, joysitck.getOnPush());
    }

    @Test
    public void testOnUp(){
        //given
        int[] counter = {0};
        diPush.mockState(DigitalState.LOW);
        joysitck.onPushUp(()->counter[0]++);

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
        joysitck.deRegisterAll();

        //then
        assertEquals(null, joysitck.getOnNorth());
        assertEquals(null, joysitck.getOnEast());
        assertEquals(null, joysitck.getOnSouth());
        assertEquals(null, joysitck.getOnEast());
        assertEquals(null, joysitck.getOnPush());

    }

    @Test
    public void testWhilePressed() throws InterruptedException {
        //given
        int samplingTime = 100;

        int[] counter = {0};

        joysitck.pushWhilePushed(samplingTime,()->counter[0]++);

        //when
        diPush.mockState(DigitalState.HIGH);

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
        assertEquals(null, joysitck.getOnNorth());
        assertEquals(null, joysitck.getOnEast());
        assertEquals(null, joysitck.getOnSouth());
        assertEquals(null, joysitck.getOnEast());
        assertEquals(null, joysitck.getOnPush());

    }

    @Test
    public void testGetDigitalInputButtonPush() {
        assertEquals(diPush, joysitck.getDigitalInputButtonPush());
    }
}
