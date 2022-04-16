package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;


/*
I2C Addressing
The ADS11x5 chips have a base 7-bit I2C address of 0x48 (1001000) and a clever addressing scheme that allows four different addresses using just one address pin (named ADR for ADdRess). To program the address, connect the address pin as follows:





*/

public class AD_Converter extends Component{
    /**
     * i2c component
     */
    private final I2C i2c;

    /**
     * Those default address are to use this class with default CrowPi setup
     */
    private static final int DEFAULT_BUS = 0x1;
    //0x48 (1001000) ADR -> GND
    private static final int DEVICE_ADDRESS_GND = 0x48;
    //0x49 (1001001) ADR -> VDD
    private static final int DEVICE_ADDRESS_VDD = 0x49;
    //0x4A (1001010) ADR -> SDA
    private static final int DEVICE_ADDRESS_SDA = 0x4A;
    //0x4B (1001011) ADR -> SCL
    private static final int DEVICE_ADDRESS_SCL = 0x4B;

    /**
     * Creates a new AD converter component with custom bus, device address
     *
     * @param pi4j   Pi4J context
     * @param bus    Custom I2C bus address
     * @param device Custom device address on I2C
     */
    public AD_Converter(Context pi4j, int bus, int device) {
        this.i2c = pi4j.create(buildI2CConfig(pi4j, bus, device));
    }

    /**
     * Creates a new AD converter component with bus 01, device address GND
     *
     * @param pi4j   Pi4J context
     */
    public AD_Converter(Context pi4j){
        this.i2c = pi4j.create(buildI2CConfig(pi4j,DEFAULT_BUS, DEVICE_ADDRESS_GND));
    }

    public int getValue(){
        byte[] buffer = new byte[3];
        buffer[0] = (byte) 0b10010001; //7bit i2c address and read/writ bit set to read (1)
        return i2c.readRegister(0,buffer);
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
