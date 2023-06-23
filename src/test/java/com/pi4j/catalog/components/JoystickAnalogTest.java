package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Duration;

import com.pi4j.catalog.ComponentTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

    @Test
    public void testStartStop(){
        //when
        joystickAnalog.start(0.05,10);
        //then
        verify(mockPotentiometerX, times(1)).startSlowContinuousReading(0.05,10);
        verify(mockPotentiometerY, times(1)).startSlowContinuousReading(0.05,10);
        verify(mockPotentiometerX, times(0)).startFastContinuousReading(0.05,10);
        verify(mockPotentiometerY, times(0)).startFastContinuousReading(0.05,10);
        //when
        joystickAnalog.stop();
        //then
        verify(mockPotentiometerX, times(1)).stopSlowContinuousReading();
        verify(mockPotentiometerY, times(1)).stopSlowContinuousReading();
        verify(mockPotentiometerX, times(0)).stopFastContinuousReading();
        verify(mockPotentiometerY, times(0)).stopFastContinuousReading();

    }

    @Test
    public void testDeregisterAll(){
        //when
        joystickAnalog.deregisterAll();
        //then
        verify(mockPotentiometerX, times(1)).deregisterAll();
        verify(mockPotentiometerY, times(1)).deregisterAll();
        verify(mockSimpleButton, times(1)).reset();
    }

    @Test
    public void testPushOnDown(){
        //when
        joystickAnalog.pushOnDown(null);
        //then
        verify(mockSimpleButton, times(1)).onDown(null);
    }

    @Test
    public void testPushOnUp(){
        //when
        joystickAnalog.pushOnUp(null);
        //then
        verify(mockSimpleButton, times(1)).onUp(null);
    }

    @Test
    public void testWhilePressed(){
        //when
        joystickAnalog.pushWhilePressed(null, Duration.ofMillis(10));
        //then
        verify(mockSimpleButton, times(1)).whilePressed(null,  Duration.ofMillis(10));
    }

    @Test
    public void testCalibrateJoystick(){
        //given
        double centerPosition = 0.5;
        double calibrationX = 0.05;
        double calibrationY = 0.1;
        when(mockPotentiometerX.singleShotGetNormalizedValue()).thenReturn(calibrationX);
        when(mockPotentiometerY.singleShotGetNormalizedValue()).thenReturn(calibrationY);
        //when
        joystickAnalog.calibrateJoystick();
        //then
        assertEquals(joystickAnalog.getX_Offset(), centerPosition-calibrationX);
        assertEquals(joystickAnalog.getY_Offset(), centerPosition-calibrationY);
    }

}
