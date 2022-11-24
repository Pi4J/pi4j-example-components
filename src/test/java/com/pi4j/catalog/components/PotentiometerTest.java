package com.pi4j.catalog.components;

import com.pi4j.catalog.ComponentTest;
import com.pi4j.catalog.components.helpers.ContinuousMeasuringException;
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
    Ads1115 mockAds1115;

    private final double voltageFromAdConverter = 2.3;

    @BeforeEach
    public void setUp(){
        //create mock ads1115
        mockAds1115 = mock(Ads1115.class);

        //set mock function ads1115
        //function returns voltage
        when(mockAds1115.singleShotAIn0()).thenReturn(voltageFromAdConverter);
        when(mockAds1115.getPga()).thenReturn(Ads1115.GAIN.GAIN_4_096V);
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
    public void testStartSlowContinuousReading(){
        potentiometer.startFastContinuousReading(0.05, 10);

        Assertions.assertThrows(ContinuousMeasuringException.class, ()->{potentiometer.startSlowContinuousReading(0.05, 10);});

        potentiometer.stopFastContinuousReading();

        potentiometer.startSlowContinuousReading(0.05,10);

        potentiometer.stopSlowContinuousReading();

    }

    @Test
    public void testStartFastContiniousReading(){
        potentiometer.startSlowContinuousReading(0.05, 10);

        Assertions.assertThrows(ContinuousMeasuringException.class, ()->{potentiometer.startFastContinuousReading(0.05, 10);});

        potentiometer.stopSlowContinuousReading();

        potentiometer.startFastContinuousReading(0.05,10);

        potentiometer.stopFastContinuousReading();

    }





}
