package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
I2C Addressing
The ADS11x5 chips have a base 7-bit I2C address of 0x48 (1001000) and a clever addressing scheme that allows four different addresses using just one address pin (named ADR for ADdRess). To program the address, connect the address pin as follows:





*/

public class ADS1115 extends Component {
    /**
     * i2c component
     */
    private final I2C i2c;


    private static final Logger LOG = LoggerFactory.getLogger(ADS1115.class);

    /**
     * device name
     */
    private final String deviceId;
    private final Context context;
    private final int i2cBus;
    private final ADS1115.GAIN gain;
    private final ADDRESS address;

    public enum ADDRESS{
        GND (0x48),
        VDD (0x49),
        SDA (0x4A),
        SCL (0x4B);
        private final int address;
        ADDRESS(int address){
            this.address = address;
        }
        public int getAddress(){return address;}
    }

    public enum GAIN {
        GAIN_6_144V(0b0000000000000000, 187.5/1_000_000),
        GAIN_4_096V(0b0000001000000000, 125.0/1_000_000),
        GAIN_2_048V(0b0000010000000000, 62.5/1_000_000),
        GAIN_1_024V(0b0000011000000000, 31.25/1_000_000),
        GAIN_0_512V(0b0000100000000000, 15.625/1_000_000),
        GAIN_0_256V(0b0000101000000000, 7.8125/1_000_000);
        private final int gain;
        private final double gainPerByte;
        GAIN(int gain, double gainPerByte) {
            this.gain = gain;
            this.gainPerByte = gainPerByte;
        }
        public int gain() {
            return gain;
        }
        public double gainPerByte() {
            return gainPerByte;
        }
    }

    private static final int CONVERSION_REGISTER = 0x00;
    private static final int CONFIG_REGISTER     = 0x01;
    private static final int LO_THRESH_REGISTER  = 0x02;
    private static final int HI_THRESH_REGISTER  = 0x03;

    private static final int A0_IN = 0b0100000000000000;
    private static final int A1_IN = 0b0101000000000000;
    private static final int A2_IN = 0b0110000000000000;
    private static final int A3_IN = 0b0111000000000000;

    private static final int CONFIG_REGISTER_TEMPLATE = 0b1000000110000011;

    /**
     * Creates a new AD converter component with custom bus, device address
     *
     * @param pi4j   Pi4J context
     * @param bus    Custom I2C bus address
     * @param address Custom device address on I2C
     */
    public ADS1115(Context pi4j, int bus, GAIN gain, ADDRESS address) {
        this.deviceId = "ADS1115";
        this.context = pi4j;
        this.i2cBus = bus;
        this.gain = gain;
        this.address = address;
        LOG.info("create i2c object");
        this.i2c = pi4j.create(buildI2CConfig(pi4j, bus, address.getAddress()));
        LOG.info("i2c object is created");
    }





    public Context getContext() {
        return context;
    }


    public int getI2CBus() {
        return i2cBus;
    }


    public String getDeviceId() {
        return deviceId;
    }


    public ADS1115.GAIN getGain() {
        return gain;
    }


    public double getAIn0() {
        return gain.gainPerByte * readIn(calculateConfig(A0_IN));
    }


    public double getAIn1() {
        return  gain.gainPerByte * readIn(calculateConfig(A1_IN));
    }


    public double getAIn2() {
        return  gain.gainPerByte * readIn(calculateConfig(A2_IN));
    }


    public double getAIn3() {
        return  gain.gainPerByte * readIn(calculateConfig(A3_IN));
    }

    private int readIn(int config) {
        i2c.writeRegisterWord(CONFIG_REGISTER, config);
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            LOG.error("Error: ", e);
        }
        int result = i2c.readRegisterWord(CONVERSION_REGISTER);
        LOG.debug("readIn: {}, raw {}", config, result);
        return result;
    }

    private int calculateConfig(int pinId) {
        return CONFIG_REGISTER_TEMPLATE | gain.gain | pinId;
    }

    /**
     * Build a I2C Configuration to use the AD convertor
     *
     * @param pi4j   PI4J Context
     * @param bus    I2C Bus address
     * @param device I2C Device address
     * @return I2C configuration
     */
    private static I2CConfig buildI2CConfig(Context pi4j, int bus, int device) {
        return I2C.newConfigBuilder(pi4j)
                .id("I2C-" + device + "@" + bus)
                .name("AD Converter")
                .bus(bus)
                .device(device)
                .build();
    }

}
