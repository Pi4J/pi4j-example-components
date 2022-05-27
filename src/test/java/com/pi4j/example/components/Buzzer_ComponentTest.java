package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import com.pi4j.example.components.helpers.PIN;
import com.pi4j.plugin.mock.provider.pwm.MockPwm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Buzzer_ComponentTest extends ComponentTest {

    private Buzzer buzzer;
    private MockPwm pwm;
    private final PIN address = PIN.PWM18;

    @BeforeEach
    void setUp() {
        buzzer = new Buzzer(pi4j, address);
        pwm = toMock(buzzer.pwm);
    }

    @Test
    public void testPwm(){
        //when
        buzzer.playSilence(100);

        //then
        assertTrue(pwm.isOff());
    }
}
