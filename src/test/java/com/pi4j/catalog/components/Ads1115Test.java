package com.pi4j.catalog.components;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.pi4j.io.i2c.I2C;

import com.pi4j.catalog.ComponentTest;

import static org.mockito.Mockito.when;

public class Ads1115Test extends ComponentTest {

    private final int i2cBus = 0x1;

    private int defaultConfigRegister;
    private int answerSingleShotAIN0ConfigRegister;
    private int answerConfigRegister;
    private static final int CONVERSION_REGISTER = 0x00;
    private static final int CONFIG_REGISTER = 0x01;
    @InjectMocks
    Ads1115 ads1115;

    @Mock
    I2C mockI2C;

    @BeforeEach
    public void setUp() {

        //setup mocks
        defaultConfigRegister = Ads1115.OperationalStatus.WRITE_START.getOs()
                | Ads1115.GAIN.GAIN_4_096V.gain()
                | Ads1115.DataRate.SPS_128.getConf()
                | Ads1115.COMP_MODE.TRAD_COMP.getCompMode()
                | Ads1115.COMP_POL.ACTIVE_LOW.getCompPol()
                | Ads1115.COMP_LAT.NON_LATCH.getLatching()
                | Ads1115.COMP_QUE.DISABLE_COMP.getCompQue();

        answerSingleShotAIN0ConfigRegister = defaultConfigRegister
                & Ads1115.OperationalStatus.CLR_CURRENT_CONF_PARAM.getOs()
                | Ads1115.OperationalStatus.READ_NO_CONV.getOs()
                | Ads1115.MultiplexerConfig.AIN0_GND.getMux()
                | Ads1115.OperationMode.SINGLE.getMode();

        when(mockI2C.writeRegisterWord(CONFIG_REGISTER, defaultConfigRegister | Ads1115.MultiplexerConfig.AIN0_GND.getMux() | Ads1115.OperationMode.SINGLE.getMode())).thenReturn(answerSingleShotAIN0ConfigRegister);

        ads1115 = new Ads1115(pi4j);
        mockI2C = ads1115.mock();
    }


}
