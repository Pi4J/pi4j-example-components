package com.pi4j.components.components;

import com.pi4j.config.exception.ConfigException;
import com.pi4j.components.ComponentTest;
import com.pi4j.components.components.helpers.ContinuousMeasuringException;
import com.pi4j.io.i2c.I2C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ADS1115_Test extends ComponentTest {

    private final int i2cBus = 0x1;

    private int defaultConfigRegister;
    private int answerSingleShotAIN0ConfigRegister;
    private int answerConfigRegister;
    private static final int CONVERSION_REGISTER = 0x00;
    private static final int CONFIG_REGISTER = 0x01;
    @InjectMocks
    ADS1115 ads1115;

    @Mock
    I2C mockI2C;

    @BeforeEach
    public void setUp() {
        mockI2C = mock(I2C.class);

        //setup mocks
        defaultConfigRegister = ADS1115.OS.WRITE_START.getOs()
                | ADS1115.GAIN.GAIN_4_096V.gain()
                | ADS1115.DR.SPS_128.getConf()
                | ADS1115.COMP_MODE.TRAD_COMP.getCompMode()
                | ADS1115.COMP_POL.ACTIVE_LOW.getCompPol()
                | ADS1115.COMP_LAT.NON_LATCH.getLatching()
                | ADS1115.COMP_QUE.DISABLE_COMP.getCompQue();

        answerSingleShotAIN0ConfigRegister = defaultConfigRegister
                & ADS1115.OS.CLR_CURRENT_CONF_PARAM.getOs()
                | ADS1115.OS.READ_NO_CONV.getOs()
                | ADS1115.MUX.AIN0_GND.getMux()
                | ADS1115.MODE.SINGLE.getMode();

        when(mockI2C.writeRegisterWord(CONFIG_REGISTER, defaultConfigRegister | ADS1115.MUX.AIN0_GND.getMux() | ADS1115.MODE.SINGLE.getMode())).thenReturn(answerSingleShotAIN0ConfigRegister);

        ads1115 = new ADS1115(pi4j, i2cBus, ADS1115.GAIN.GAIN_4_096V, ADS1115.ADDRESS.GND, 1, mockI2C);

    }

    @Test
    public void testGetContext() {
        assertEquals(pi4j, ads1115.getContext());
    }

    @Test
    public void testGetI2CBus() {
        assertEquals(i2cBus, ads1115.getI2CBus());
    }

    @Test
    public void testGetDeviceId() {
        assertEquals("ADS1115", ads1115.getDeviceId());
    }

    @Test
    public void testGetGain() {
        assertEquals(ADS1115.DR.SPS_128.getSpS(), ads1115.getSamplingRate());
    }

    @Test
    public void testReadConversionRegister() {
        //given
        int conversionRegisterValue = 16;
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        //then
        assertEquals(conversionRegisterValue, ads1115.readConversionRegister());
    }

    @Test
    public void testWriteConfigRegister() {
        //given
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN0_GND.getMux() | ADS1115.MODE.SINGLE.getMode();
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);
        //then
        assertEquals(answerSingleShotAIN0ConfigRegister, ads1115.writeConfigRegister( defaultConfigRegister | ADS1115.MUX.AIN0_GND.getMux() | ADS1115.MODE.SINGLE.getMode()));
    }

    @Test
    public void testSingleShotAIn0(){
        //given
        int conversionRegisterValue =16000;
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN0_GND.getMux() | ADS1115.MODE.SINGLE.getMode();
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);
        //then
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), ads1115.singleShotAIn0());
    }

    @Test
    public void testSingleShotAIn0Exception(){
        //when
        ads1115.startFastContinuousReading(0,0.05,10);
        //then
        assertThrows(ContinuousMeasuringException.class, () -> ads1115.singleShotAIn1());
    }

    @Test
    public void testSingleShotExceptionReadWrongChanel(){
        //given
        int conversionRegisterValue =16000;
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN0_GND.getMux() | ADS1115.MODE.SINGLE.getMode();
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);
        //then
        assertThrows( ConfigException.class, () -> ads1115.singleShotAIn1());

    }

    @Test
    public void testSingleShotAIn1(){
        //given
        int conversionRegisterValue =16000;
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN1_GND.getMux() | ADS1115.MODE.SINGLE.getMode();
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);
        //then
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), ads1115.singleShotAIn1());
    }

    @Test
    public void testSingleShotAIn1Exception(){
        //when
        ads1115.startFastContinuousReading(0,0.05,10);
        //then
        assertThrows(ContinuousMeasuringException.class, () -> ads1115.singleShotAIn1());
    }

    @Test
    public void testSingleShotAIn2(){
        //given
        int conversionRegisterValue =16000;
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN2_GND.getMux() | ADS1115.MODE.SINGLE.getMode();
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);
        //then
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), ads1115.singleShotAIn2());
    }

    @Test
    public void testSingleShotAIn2Exception(){
        //when
        ads1115.startFastContinuousReading(0,0.05,10);
        //then
        assertThrows(ContinuousMeasuringException.class, () -> ads1115.singleShotAIn2());
    }

    @Test
    public void testSingleShotAIn3(){
        //given
        int conversionRegisterValue =16000;
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN3_GND.getMux() | ADS1115.MODE.SINGLE.getMode();
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);
        //then
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), ads1115.singleShotAIn3());
    }

    @Test
    public void testSingleShotAIn3Exception(){
        //when
        ads1115.startFastContinuousReading(0,0.05,10);
        //then
        assertThrows(ContinuousMeasuringException.class, () -> ads1115.singleShotAIn3());
    }

    @Test
    public void testFastContinuousReading(){
        //given
        int conversionRegisterValue =16000;
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN3_GND.getMux() | ADS1115.MODE.CONTINUOUS.getMode();
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);

        AtomicReference<Double> actualValue = new AtomicReference<>((double) 0);
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        ads1115.setConsumerFastRead((value)->{
            actualValue.set(value);
            counter.set(counter.get()+1);
        });

        //when
        ads1115.startFastContinuousReading(0,0.05,10);

        //then
        assertEquals(0, counter.get());
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(1, counter.get());

        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // no new value in conversion register
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(1, counter.get());

        //when
        conversionRegisterValue =18000;
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);

        //then
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //new value in conversion register
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(2, counter.get());


        //when
        int deltaConversionRegister = 20;
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue + deltaConversionRegister);

        //then
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //new value in conversion register is too small for value change event
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(2, counter.get());
    }

    @Test
    public void testFastContinuousReadingExceptionFrequency(){
        //then
        assertThrows(ContinuousMeasuringException.class, () -> ads1115.startFastContinuousReading(0,0.05, 200));
    }

    @Test
    public void testFastContinuousReadingExceptionReadingAlreadyRunning(){
        //when
        ads1115.startFastContinuousReading(0,0.05,10);
        //then
        ads1115.stopFastContinuousReading();
        ads1115.startFastContinuousReading(0,0.05,10);
        assertThrows(ContinuousMeasuringException.class, () -> ads1115.startFastContinuousReading(0,0.05, 10));
    }

    @Test
    public void testSlowContinuousReading(){
        //given
        int conversionRegisterValue =16000;
        answerConfigRegister = defaultConfigRegister | ADS1115.MUX.AIN0_GND.getMux() | ADS1115.MODE.SINGLE.getMode();
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);
        when(mockI2C.readRegisterWord(CONFIG_REGISTER)).thenReturn(answerConfigRegister);

        AtomicReference<Double> actualValue = new AtomicReference<>((double) 0);
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        ads1115.setConsumerSlowReadChannel0((value)->{
            actualValue.set(value);
            counter.set(counter.get()+1);
        });

        //when
        ads1115.startSlowContinuousReading(0,0.05,10);

        //then
        assertEquals(0, counter.get());
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(1, counter.get());

        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // no new value in conversion register
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(1, counter.get());

        //when
        conversionRegisterValue =18000;
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue);

        //then
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //new value in conversion register
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(2, counter.get());


        //when
        int deltaConversionRegister = 20;
        when(mockI2C.readRegisterWord(CONVERSION_REGISTER)).thenReturn(conversionRegisterValue + deltaConversionRegister);

        //then
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //new value in conversion register is too small for value change event
        assertEquals(conversionRegisterValue * ads1115.getPga().gainPerBit(), actualValue.get());
        assertEquals(2, counter.get());
    }

    @Test
    public void testSlowContinuousReadingExceptionFrequency(){
        //then
        assertThrows(ContinuousMeasuringException.class, () -> ads1115.startSlowContinuousReading(0,0.05, 200));
    }
}
