package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pi4j.plugin.mock.provider.pwm.MockPwm;

import com.pi4j.catalog.ComponentTest;
import com.pi4j.catalog.components.base.PIN;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuzzerComponentTest extends ComponentTest {

    private Buzzer buzzer;
    private MockPwm pwm;
    private final PIN address = PIN.PWM18;

    @BeforeEach
    public void setUp() {
        buzzer = new Buzzer(pi4j, address);
        pwm = buzzer.mock();
    }

    @Test
    public void testPlayTone() {
        // when
        buzzer.playTone(1000);

        // then
        assertTrue(pwm.isOn());
        assertEquals(1000, pwm.frequency());
    }

    @Test
    public void testPlayToneWithDuration() {
        // when
        buzzer.playTone(1000, 10);

        // then
        assertTrue(pwm.isOff());
        assertEquals(1000, pwm.frequency());
    }

    @Test
    public void testPlayToneWithInterrupt() {
        // when
        Thread.currentThread().interrupt();
        buzzer.playTone(1000, 5000);

        // then
        assertTrue(pwm.isOff());
    }

    @Test
    public void testPlaySilence() {
        // given
        buzzer.playTone(1000);

        // when
        buzzer.off();

        // then
        assertTrue(pwm.isOff());
    }

    @Test
    public void testPlaySilenceInterrupt() {
        // given
        buzzer.playTone(1000);

        // when
        Thread.currentThread().interrupt();
        buzzer.pause(5000);

        // then
        assertTrue(pwm.isOff());
    }
}
