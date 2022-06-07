package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

public class JoystickAnalogTest extends ComponentTest {

    @InjectMocks
    JoystickAnalog joystickAnalog;

    @Mock
    Potentiometer mockPotentiometerX;

    @Mock
    Potentiometer mockPotentiometerY;

    @Mock
    SimpleButton mockSimpleButton;

    @BeforeEach
    public void setup(){
        mockPotentiometerX = mock(Potentiometer.class);
        mockPotentiometerY = mock(Potentiometer.class);
        mockSimpleButton = mock(SimpleButton.class);
        joystickAnalog = new JoystickAnalog(mockPotentiometerX, mockPotentiometerY, true, mockSimpleButton);
    }

}
