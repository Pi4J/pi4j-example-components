package com.pi4j.catalog.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.pi4j.catalog.ComponentTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PotentiometerTest extends ComponentTest {

    @InjectMocks
    Potentiometer potentiometer;
    @Mock
    Ads1115 mockAds1115;

    private final double voltageFromAdConverter = 2.3;

    @BeforeEach
    public void setUp(){
        //create mock ads1115
        mockAds1115 = mock(Ads1115.class);

        //set mock function ads1115
        //function returns voltage
        when(mockAds1115.readValue(Ads1115.Channel.A0)).thenReturn(voltageFromAdConverter);
        //when(mockAds1115.setConsumerFastRead()).thenReturn();
        //when(mockAds1115.setConsumerSlowReadChannel0()).thenReturn();

        //create potentiometer with mock ads1115 class
        potentiometer = new Potentiometer(mockAds1115, Ads1115.Channel.A0);
    }


    @Test
    public void testSingleShotGetVoltage(){
        Assertions.assertEquals(voltageFromAdConverter, potentiometer.readCurrentVoltage());
    }








}
