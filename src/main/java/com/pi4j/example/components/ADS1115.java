package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ADS1115 extends Component {
    /**
     * logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(ADS1115.class);

    /**
     * i2c component
     */
    private final I2C i2c;

    /**
     * device name
     */
    private final String deviceId;

    /**
     * pi4j context
     */
    private final Context context;

    /**
     * bus number
     */
    private final int i2cBus;

    /**
     * programmable gain amplifier
     */
    private final ADS1115.GAIN gain;

    /**
     * device address
     */
    // TODO: 01.05.22 is adress stored in enum? 
    private final ADDRESS address;

    /**
     * The Conversion register contains the result of the last conversion.
     */
    private static final int CONVERSION_REGISTER = 0x00;
    /**
     * The Config register is used to change the ADS1115 operating modes and query the status of the device.
     */
    private static final int CONFIG_REGISTER     = 0x01;
    /**
     * Lo_thresh set the lower threshold values used for the comparator function
     */
    private static final int LO_THRESH_REGISTER  = 0x02;
    /**
     * Hi_thresh set the high threshold values used for the comparator function
     */
    private static final int HI_THRESH_REGISTER  = 0x03;

    /**
     * Config register default configuration
     *
     * OS -> 1 : Start a single conversion
     * MUX -> 000 : AINP = AIN0 and AINN = AIN1
     * PGA -> 000 : FSR = ±6.144 V(
     * MODE -> 1 : Single-shot mode or power-down state
     * DR -> 100 : 128 SPS
     * COMP_MODE -> 0 : Traditional comparator
     * COMP_POL -> 0 : Active low
     * COMP_LAT -> 0 : Nonlatching comparator . The ALERT/RDY pin does not latch when asserted
     * COMP_QUE -> 11 : Disable comparator and set ALERT/RDY pin to high-impedance
     */
    private static final int CONFIG_REGISTER_TEMPLATE = 0b1000000110000011;

    /**
     * I2C Address Selection
     * The ADS1115 has one address pin, ADDR, that configures the I2C address of the device. This pin can be
     * connected to GND, VDD, SDA, or SCL, allowing for four different addresses to be selected with one pin.
     * Use the GND, VDD and SCL addresses first. If SDA is used as the device address, hold the SDA line low
     * for at least 100 ns after the SCL line goes low to make sure the device decodes the address correctly
     * during I2C communication.
     *
     * Address that can be used
     * {@link #GND}
     * {@link #VDD}
     * {@link #SDA}
     * {@link #SCL}
     */
    public enum ADDRESS{
        /**
         * Device address if pin is connected to GND
         */
        GND (0x48),
        /**
         * Device address if pin is connected to VDD
         */
        VDD (0x49),
        /**
         * Device address if pin is connected to SDA
         */
        SDA (0x4A),
        /**
         * Device address if pin is connected to SCL
         */
        SCL (0x4B);
        /**
         * device address on I2C
         */
        private final int address;

        /**
         * Set the address for a device on an I2C bus
         * @param address device address on I2C
         */
        ADDRESS(int address){
            this.address = address;
        }

        /**
         * Retunrs the address from the device on an I2C bus
         *
         * @return Returns the address form the device
         */
        public int getAddress(){return address;}
    }

    /**
     * A programmable gain amplifier (PGA) is implemented before the ΔΣ ADC of the ADS1115. The
     * full-scale range is configured by bits PGA[2:0] in the Config register and can be set to ±6.144 V, ±4.096 V,
     * ±2.048 V, ±1.024 V, ±0.512 V, ±0.256 V.
     *
     * programmable gain amplifier that can be used
     * {@link #GAIN_6_144V}
     * {@link #GAIN_4_096V}
     * {@link #GAIN_2_048V}
     * {@link #GAIN_1_024V}
     * {@link #GAIN_0_512V}
     * {@link #GAIN_0_256V}
     *
     */
    public enum GAIN {
        /**
         * 000 : Full-Scale Range (FSR)  = ±6.144 V
         */
        GAIN_6_144V(0b0000000000000000, 187.5/1_000_000),
        /**
         * 001 : FSR = ±4.096 V
         */
        GAIN_4_096V(0b0000001000000000, 125.0/1_000_000),
        /**
         * 010 : FSR = ±2.048 V
         */
        GAIN_2_048V(0b0000010000000000, 62.5/1_000_000),
        /**
         * 011 : FSR = ±1.024 V
         */
        GAIN_1_024V(0b0000011000000000, 31.25/1_000_000),
        /**
         * 100 : FSR = ±0.512 V
         */
        GAIN_0_512V(0b0000100000000000, 15.625/1_000_000),
        /**
         * 101 : FSR = ±0.256 V
         */
        GAIN_0_256V(0b0000101000000000, 7.8125/1_000_000);
        /**
         * bit structure for configuration
         */
        private final int gain;
        /**
         * gain per bit
         */
        private final double gainPerBit;

        /**
         * Set bit structure for configuration and resolution (gain per bit)
         *
         * @param gain configuration for gain
         * @param gainPerBit resolution
         */
        GAIN(int gain, double gainPerBit) {
            this.gain = gain;
            this.gainPerBit = gainPerBit;
        }

        /**
         * Returns bit structure for gain configuration
         *
         * @return bit structure for configuration
         */
        public int gain() {
            return gain;
        }

        /**
         * Return resolution gain per bit
         *
         * @return gain per bit
         */
        public double gainPerBit() {
            return gainPerBit;
        }
    }

    /**
     * Input multiplexer configuration
     * 100 : AINP = AIN0 and AINN = GND
     */
    private static final int A0_IN = 0b0100000000000000;
    /**
     * Input multiplexer configuration
     * 101 : AINP = AIN1 and AINN = GND
     */
    private static final int A1_IN = 0b0101000000000000;
    /**
     * Input multiplexer configuration
     * 110 : AINP = AIN2 and AINN = GND
     */
    private static final int A2_IN = 0b0110000000000000;
    /**
     * Input multiplexer configuration
     * 111 : AINP = AIN3 and AINN = GND
     */
    private static final int A3_IN = 0b0111000000000000;

    /**
     * Creates a new AD converter component with custom bus, device address
     *
     * @param pi4j   Pi4J context
     * @param bus    Custom I2C bus address
     * @param gain   Custom gain amplifier
     * @param address Custom device address on I2C
     */
    public ADS1115(Context pi4j, int bus, GAIN gain, ADDRESS address) {
        this.deviceId = "ADS1115";
        this.context = pi4j;
        this.i2cBus = bus;
        this.gain = gain;
        this.address = address;
        this.i2c = pi4j.create(buildI2CConfig(pi4j, bus, address.getAddress()));
    }

    /**
     * Return pi4j context
     * @return pi4j context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Return bus address
     * @return bus address
     */
    public int getI2CBus() {
        return i2cBus;
    }

    /**
     * Return device name
     * @return device name
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Return GAIN object with bit structure for configuration and resolution (gain per bit)
     * @return GAIN object
     */
    public GAIN getGain() {
        return gain;
    }

    /**
     * Returns voltage value from AIn0
     * @return double voltage
     */
    public double getAIn0() {
        return gain.gainPerBit * readIn(calculateConfig(A0_IN));
    }

    /**
     * Returns voltage value from AIn1
     * @return double voltage
     */
    public double getAIn1() {
        return  gain.gainPerBit * readIn(calculateConfig(A1_IN));
    }

    /**
     * Returns voltage value from AIn2
     * @return double voltage
     */
    public double getAIn2() {
        return  gain.gainPerBit * readIn(calculateConfig(A2_IN));
    }

    /**
     * Returns voltage value from AIn3
     * @return double voltage
     */
    public double getAIn3() {
        return  gain.gainPerBit * readIn(calculateConfig(A3_IN));
    }

    /**
     * Sends a request to device and wait for response
     *
     * @param config Configuration for config register
     * @return int conversion register
     */
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

    /**
     * Setup configuration for config register
     *
     * @param mux input multiplexer configuration
     * @return Configuration for config register
     */
    private int calculateConfig(int mux) {
        return CONFIG_REGISTER_TEMPLATE | gain.gain | mux;
    }

    /**
     * Build a I2C Configuration to use the AD convertor
     *
     * @param pi4j   PI4J Context
     * @param bus    I2C Bus address
     * @param device I2C Device address
     * @return I2C configuration
     */
    // TODO: 01.05.22 does this has to be static? otherwise variable could be used for name
    private static I2CConfig buildI2CConfig(Context pi4j, int bus, int device) {
        return I2C.newConfigBuilder(pi4j)
                .id("I2C-" + device + "@" + bus)
                .name("AD Converter")
                .bus(bus)
                .device(device)
                .build();
    }

}
