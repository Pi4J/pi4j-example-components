package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.plugin.mock.provider.pwm.MockPwm;

import com.pi4j.catalog.ComponentTest;
import com.pi4j.catalog.components.base.PIN;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServoTest extends ComponentTest {
    private ServoMotor servo;
    private MockPwm pwm;
    private final PIN address = PIN.PWM18;

    @BeforeEach
    public void setUp() {
        servo = new ServoMotor(pi4j, address);
        pwm = toMock(servo.getPwm());
    }

    @Test
    public void test_init(){
        //when
        servo = new ServoMotor(pi4j, PIN.PWM19, 0, 180, 2, 12);

        //then
        assertEquals(0, servo.getMinAngle());
        assertEquals(180, servo.getMaxAngle());
    }

    @Test
    public void test_angle(){
        //when
        servo.setAngle(servo.getMinAngle());

        //then
        assertEquals(2, servo.getPwm().getDutyCycle());

        //when
        servo.setAngle(servo.getMaxAngle());

        //then
        assertEquals(12, servo.getPwm().getDutyCycle());
    }
}
