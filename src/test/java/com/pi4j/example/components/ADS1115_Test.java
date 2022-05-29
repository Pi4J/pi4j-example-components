package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import com.pi4j.io.i2c.I2C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ADS1115_Test extends ComponentTest {

    private final int i2cBus = 0x1;

    @InjectMocks
    ADS1115 ads1115;

    @Mock
    I2C mockI2C;

    @BeforeEach
    public void setUp(){

        ads1115 = new ADS1115(pi4j, i2cBus, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND,1);

    }

    @Test
    public void testGetContext(){
        assertEquals(pi4j, ads1115.getContext());
    }

    @Test
    public void testGetI2CBus(){
        assertEquals(i2cBus, ads1115.getI2CBus());
    }

    @Test
    public void testGetDeviceId(){
        assertEquals("ADS1115", ads1115.getDeviceId());
    }

    @Test
    public void testGetGain(){
        assertEquals(ADS1115.DR.SPS_128.getSpS(), ads1115.getSamplingRate());
    }

}
