package com.pi4j.example.components;

import com.pi4j.example.ComponentTest;
import com.pi4j.example.helpers.ContiniousMeasuringException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class PotentiometerTest extends ComponentTest {

    @InjectMocks
    Potentiometer potentiometer;
    @Mock
    ADS1115 mockAds1115;

    private double voltageFromAdConverter = 2.3;

    @BeforeEach
    public void setUp(){
        //create mock ads1115
        mockAds1115 = mock(ADS1115.class);

        //set mock function ads1115
        //function returns voltage
        when(mockAds1115.singleShotAIn0()).thenReturn(voltageFromAdConverter);
        when(mockAds1115.getPga()).thenReturn(ADS1115.GAIN.GAIN_4_096V);
        //when(mockAds1115.setConsumerFastRead()).thenReturn();
        //when(mockAds1115.setConsumerSlowReadChannel0()).thenReturn();

        //create potentiometer with mock ads1115 class
        potentiometer = new Potentiometer(mockAds1115, 0,3.3);
    }


    @Test
    public void testSingleShotGetVoltage(){
        Assertions.assertEquals(voltageFromAdConverter, potentiometer.singleShotGetVoltage());
    }

    @Test
    public void testSingleShotGetNormalizedValue(){
        Assertions.assertEquals(voltageFromAdConverter/potentiometer.getMaxValue() , potentiometer.singleShotGetNormalizedValue());
    }

    @Test
    public void testStartSlowContiniousReading(){
        potentiometer.startFastContiniousReading(0.05, 10);

        Assertions.assertThrows(ContiniousMeasuringException.class, ()->{potentiometer.startSlowContiniousReading(0.05, 10);});

        potentiometer.stopFastContiniousReading();

        potentiometer.startSlowContiniousReading(0.05,10);

        potentiometer.stopSlowContiniousReading();

    }

    @Test
    public void testStartFastContiniousReading(){
        potentiometer.startSlowContiniousReading(0.05, 10);

        Assertions.assertThrows(ContiniousMeasuringException.class, ()->{potentiometer.startFastContiniousReading(0.05, 10);});

        potentiometer.stopSlowContiniousReading();

        potentiometer.startFastContiniousReading(0.05,10);

        potentiometer.stopFastContiniousReading();

    }





}