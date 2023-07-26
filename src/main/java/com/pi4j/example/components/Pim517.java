package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Pim517 extends Component{
    /**
     * The Default BUS and Device Address.
     * On the PI, you can look it up with the Command 'sudo i2cdetect -y 1'
     */
    private static final int DEFAULT_BUS = 0x1;
    private static final int DEFAULT_DEVICE = 0x27;

    private final Context pi4j;
    private final I2C i2cDevice;

    private final int busNum;
    private final int address;

    private final ArrayList<DigitalInOut> pins;

    // These values encode our desired pin function: IO, ADC, PWM
    // alongside the GPIO MODE for that port and pin (section 8.1)
    // the 5th bit additionally encodes the default output state
    private final int PIN_MODE_IO = 0b00000;   // General IO mode, IE: not ADC or PWM
    private final int PIN_MODE_QB = 0b00000;   // Output, Quasi-Bidirectional mode
    private final int PIN_MODE_PP = 0b00001;   // Output, Push-Pull mode
    private final int PIN_MODE_IN = 0b00010;   // Input-only (high-impedance)
    private final int PIN_MODE_PU = 0b10000;   // Input (with pull-up)
    private final int PIN_MODE_OD = 0b00011;   // Output, Open-Drain mode
    private final int PIN_MODE_PWM = 0b00101;  // PWM, Output, Push-Pull mode
    private final int PIN_MODE_ADC = 0b01010;  // ADC, Input-only (high-impedance)

    private final int REG_P0 = 0x40;       // protect_bits 2 // Bit addressing
    private final int REG_P1 = 0x50;       // protect_bits 3 6 // Bit addressing
    private final int REG_CAPCON0 = 0x52;
    private final int REG_CAPCON1 = 0x53;
    private final int REG_CAPCON2 = 0x54;
    private final int REG_P2 = 0x60;       // Bit addressing
    private final int REG_P3 = 0x70;       // Bit addressing
    private final int REG_INT_MASK_P0 = 0x00;
    private final int REG_INT_MASK_P1 = 0x01;
    private final int REG_INT_MASK_P2 = 0x02;
    private final int REG_INT_MASK_P3 = 0x03;

    private final int REG_VERSION = 0xfc;
    private final int REG_ADDR = 0xfd;

    private final int REG_CTRL = 0xfe;     // 0 = Sleep, 1 = Reset, 2 = Read Flash, 3 = Write Flash, 4 = Addr Unlock
    private final int MASK_CTRL_SLEEP = 0x1;
    private final int MASK_CTRL_RESET = 0x2;
    private final int MASK_CTRL_FREAD = 0x4;
    private final int MASK_CTRL_FWRITE = 0x8;
    private final int MASK_CTRL_ADDRWR = 0x10;

    /**
     * CTOR
     * @param pi4j        Context
     */
    public Pim517(Context pi4j) {
        this(pi4j, DEFAULT_BUS, DEFAULT_DEVICE);
    }

    /**
     * CTOR
     * @param pi4j        Context
     */
    public Pim517(Context pi4j, int bus, int device) {
        this.pi4j = pi4j;
        this.busNum = bus;
        this.address = device;
        this.i2cDevice = pi4j.create(buildI2CConfig(pi4j, bus, device));
        this.pins = new ArrayList<>();
        this.init();
    }

    /**
     * Initialization of the registers and the pins
     */
    private void init(){
        for (int i = 0; i < 14; i++) {
            this.pins.add(new DigitalInOut(this, i));
        }
    }

    /**
     * Registers the I2C Component on the PI4J
     *
     * @param pi4j   The Context
     * @param bus    The I2C Bus
     * @param device The I2C Device
     * @return A Provider of an I2C Bus
     */
    private I2CConfig buildI2CConfig(Context pi4j, int bus, int device) {
        return I2C.newConfigBuilder(pi4j)
                .id("I2C-" + device + "@" + bus)
                .name("MCP23017")
                .bus(bus)
                .device(device)
                .build();
    }

    /**
     * Reading from the register address, 16 bits.
     * @param register the desired register
     * @return an int consisting of 16 bits
     */
    private int read16(int register){
        var reg = this.i2cDevice.register(register);

        return reg.writeReadWord(register) & 0xffff;
    }

    /**
     * Reading from the register address, 12 bits.
     * @param register the desired register
     * @return an int consisting of 12 bits
     */
    private int read12(int register){
        var reg = this.i2cDevice.register(register);

        return reg.writeReadWord(register) & 0xfff;
    }

    /**
     * Reading from the register address, 8 bits, 1 byte.
     * @param register the desired register
     * @return 8 bits, a byte
     */
    private byte read8(int register){
        var reg = this.i2cDevice.register(register);

        return (byte) (reg.writeReadWord(register) & 0xff);
    }

    /**
     * Writing to the register address, 16 bits. the A side is switched with the B side,
     * so the 8 bits from b are coming first, then the 8 bits from a
     *
     * @param register the desired register
     * @param value the 16bit value to write
     */
    private void write16(int register, int value){
        var reg = this.i2cDevice.register(register);

        byte first8 = (byte) (value & 0xff);
        byte sec8 = (byte) (value & 0xff00 >> 8);

        reg.write(first8 << 8 | sec8);
    }

    /**
     * Writing to the register address, 8 bits, 1 byte
     * @param register the desired register
     * @param value the 8bit value to write
     */
    private void write8(int register, byte value){
        var reg = this.i2cDevice.register(register);

        reg.write(value);
    }

    /**
     * Getting the PIN Information
     *
     * @param pin pin from 0 to 15
     * @return the pin itself
     */
    public DigitalInOut getPin(int pin){
        if(pin > 16 || pin < 0){
            logError("PIN " + pin + " does not exist.");
        }
        return pins.get(pin);
    }

    private get_pin_regs(int pin){

    }

    public class DigitalInOut{

        public enum Direction{
            INPUT(true),
            OUTPUT(false);

            private final boolean isInput;

            Direction(boolean isInput){
                this.isInput = isInput;
            }

            public boolean direction(){
                return this.isInput;
            }
        }
        private final int pin;
        private boolean direction;
        private boolean value;
        private boolean pullup;
        private boolean invert_polarity;
        private final Pim517 pim;

        public DigitalInOut(Pim517 pim, int pin){
            this.pim = pim;
            this.pin = pin;
            this.direction = Direction.INPUT.direction();
            this.value = false;
            this.pullup = true;
            this.invert_polarity = false;

            init();
        }

        private void init(){
            this.value = this.value();
            this.pullup = this.isPullup();
            this.invert_polarity = this.invert_polarity();
            this.direction = this.direction();
        }

        /**
         * Switch the pin state to a digital output with the provided starting
         * value (True/False for high or low, default is False/low).
         */
        public void switch_to_output(boolean value){
            this.direction = Direction.OUTPUT.direction();
            this.value = value;
        }

        /**
         * Switch the pin state to a digital input with the provided starting
         * pull-up resistor state (optional, no pull-up by default) and input polarity.  Note that
         * pull-down resistors are NOT supported!
         */
        public void switch_to_input(boolean pullup, boolean invert_polarity){
            this.direction = Direction.INPUT.direction();
            this.pullup = pullup;
            this.invert_polarity = value;
        }

        /**
         * The value of the pin, either True for high or False for
         * low.  Note you must configure as an output or input appropriately
         * before reading and writing this value.
         */
        public boolean value(){
            return get_bit(pim.gpio(), pin);
        }

        /**
         * Setter
         * @param val setting the input
         */
        public void setValue(boolean val){
            if(val){
                this.pim.gpio(enable_bit(this.pim.gpio(), pin));
            }else{
                this.pim.gpio(clear_bit(this.pim.gpio(), pin));
            }
        }

        /**
         * The direction of the pin, either True for an input or
         * False for an output.
         */
        public boolean direction(){
            if(get_bit(this.pim.iodir(), pin)){
                return Direction.INPUT.direction();
            }
            return Direction.OUTPUT.direction();
        }

        /**
         * Setter
         * @param direction setting the direction, true for Input
         */
        public void setDirection(Direction direction){
            if(direction == Direction.INPUT){
                this.pim.iodir(enable_bit(this.pim.iodir(), pin));
            } else if (direction == Direction.OUTPUT) {
                this.pim.iodir(clear_bit(this.pim.iodir(), pin));
            }else{
                logError("Direction not specified");
            }
        }

        /**
         * Getter
         * Enable or disable internal pull-up resistors for this pin.  A
         * value of digitalio.Pull.UP will enable a pull-up resistor, and None will
         * disable it.  Pull-down resistors are NOT supported!
         */
        public boolean isPullup(){
            return get_bit(this.pim.gppu(), pin);
        }

        /**
         * Setter
         */
        public void setPullup(boolean val){
            if(val){
                this.pim.gppu(enable_bit(this.pim.gppu(), pin));
            }else{
                this.pim.gppu(clear_bit(this.pim.gppu(), pin));
            }
        }

        /**
         * The polarity of the pin, either True for an Inverted or
         * False for a normal.
         */
        public boolean invert_polarity(){
            return get_bit(this.pim.ipol(), pin);
        }

        public void invert_polarity(boolean val){
            if(val){
                this.pim.ipol(enable_bit(this.pim.ipol(), pin));
            }else{
                this.pim.ipol(clear_bit(this.pim.ipol(), pin));
            }
        }

        /**
         * Helper Functions for bitwise operations
         * @param val mostly the register, a 16 bit int representing each pin
         * @param bit which bit of the register
         * @return
         */
        private boolean get_bit(int val, int bit){
            return (val & (1 << bit)) > 0;
        }

        private int enable_bit(int val, int bit){
            return val | (1 << bit);
        }

        private int clear_bit(int val, int bit){
            return val & ~(1 << bit);
        }
    }
}
