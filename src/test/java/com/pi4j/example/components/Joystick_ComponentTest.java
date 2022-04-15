package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Joystick_ComponentTest extends ComponentTest {

    protected Joystick joystick;
    protected Joystick joystickWithPush;

    @BeforeEach
    void setUp(){
        joystick = new Joystick(pi4j,5,6,13,19);
        joystickWithPush = new Joystick(pi4j, 5,6,13,19,26);

    }

    @Test
    void testGetDigitalInputJoystick(){
        Assertions.assertEquals(5, joystick.getDigitalInputButtonUp());
        Assertions.assertEquals(6, joystick.getDigitalInputButtonLeft());
        Assertions.assertEquals(13, joystick.getDigitalInputButtonDown());
        Assertions.assertEquals(19, joystick.getDigitalInputButtonRight());
        Assertions.assertEquals(null, joystick.getDigitalInputButtonPush());
    }

    @Test
    void testGetDigitalInputJoystickWithPush(){
        Assertions.assertEquals(5, joystick.getDigitalInputButtonUp());
        Assertions.assertEquals(6, joystick.getDigitalInputButtonLeft());
        Assertions.assertEquals(13, joystick.getDigitalInputButtonDown());
        Assertions.assertEquals(19, joystick.getDigitalInputButtonRight());
        Assertions.assertEquals(26, joystick.getDigitalInputButtonPush());
    }
}
