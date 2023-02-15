package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;

import java.util.ArrayList;

public class Mcp23017 extends Component{
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

    private final int _IODIRA = 0x00;
    private final int _IODIRB= 0x01;
    private final int _IPOLA = 0x02;
    private final int _IPOLB = 0x03;
    private final int _GPINTENA = 0x04;
    private final int _DEFVALA = 0x06;
    private final int _INTCONA = 0x08;
    private final int _IOCON = 0x0A;
    private final int _GPPUA = 0x0C;
    private final int _GPPUB = 0x0D;
    private final int _GPIOA = 0x12;
    private final int _GPIOB = 0x13;
    private final int _INTFA = 0x0E;
    private final int _INTFB = 0x0F;
    private final int _INTCAPA = 0x10;
    private final int _INTCAPB = 0x11;

    /**
     * CTOR
     * @param pi4j        Context
     */
    public Mcp23017(Context pi4j) {
        this(pi4j, DEFAULT_BUS, DEFAULT_DEVICE);
    }

    /**
     * CTOR
     * @param pi4j        Context
     */
    public Mcp23017(Context pi4j, int bus, int device) {
        this.pi4j = pi4j;
        this.busNum = bus;
        this.address = device;
        this.i2cDevice = pi4j.create(buildI2CConfig(pi4j, bus, device));
        this.pins = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
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

    private int read_int(int register){
        var reg = this.i2cDevice.register(register);

        byte first8 = (byte) (reg.readWord() & 0xff);
        byte sec8 = (byte) (reg.readWord() & 0xff00 >> 8);

        return first8 << 8 | sec8;
    }

    private byte read_u8(int register){
        var reg = this.i2cDevice.register(register);

        return reg.readByte();
    }

    private void write_int(int register, int value){
        var reg = this.i2cDevice.register(register);

        byte first8 = (byte) (value & 0xff);
        byte sec8 = (byte) (value & 0xff00 >> 8);

        reg.write(first8 << 8 | sec8);
    }

    private void write_u8(int register, byte value){
        var reg = this.i2cDevice.register(register);

        reg.write(value);
    }

    public DigitalInOut getPin(int pin){
        if(pin > 16 || pin < 0){
            logError("PIN " + pin + " does not exist.");
        }
        return pins.get(pin);
    }

    /**
     * The raw GPIO output register.  Each bit represents the
     * output value of the associated pin (0 = low, 1 = high), assuming that
     * pin has been configured as an output previously.
     */
    public int gpio(){
        return this.read_int(_GPIOA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void gpio(int val){
        write_int(_GPIOA, val);
    }

    /**
     * The raw GPIO A output register.  Each bit represents the
     * output value of the associated pin (0 = low, 1 = high), assuming that
     * pin has been configured as an output previously.
     */
    public byte gpioA(){
        return this.read_u8(_GPIOA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void gpioA(byte val){
        write_u8(_GPIOA, val);
    }

    /**
     * The raw GPIO B output register.  Each bit represents the
     * output value of the associated pin (0 = low, 1 = high), assuming that
     * pin has been configured as an output previously.
     */
    public byte gpioB(){
        return this.read_u8(_GPIOB);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void gpioB(byte val){
        write_u8(_GPIOB, val);
    }

    /**
     * The raw IODIR direction register.  Each bit represents
     * direction of a pin, either 1 for an input or 0 for an output mode.
     */
    public int iodir(){
        return this.read_int(_IODIRA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void iodir(int val){
        write_int(_IODIRA, val);
    }

    /**
     * The raw GPIO A output register.  Each bit represents the
     * output value of the associated pin (0 = low, 1 = high), assuming that
     * pin has been configured as an output previously.
     */
    public byte iodirA(){
        return this.read_u8(_IODIRA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void iodirA(byte val){
        write_u8(_IODIRA, val);
    }

    /**
     * The raw GPIO B output register.  Each bit represents the
     * output value of the associated pin (0 = low, 1 = high), assuming that
     * pin has been configured as an output previously.
     */
    public byte iodirB(){
        return this.read_u8(_IODIRB);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void iodirB(byte val){
        write_u8(_IODIRB, val);
    }

    /**
     * The raw GPPU pull-up register.  Each bit represents
     * if a pull-up is enabled on the specified pin (1 = pull-up enabled,
     * 0 = pull-up disabled).  Note pull-down resistors are NOT supported!
     */
    public int gppu(){
        return this.read_int(_GPPUA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void gppu(int val){
        write_int(_GPPUA, val);
    }

    /**
     * The raw GPPU A pull-up register.  Each bit represents
     * if a pull-up is enabled on the specified pin (1 = pull-up enabled,
     * 0 = pull-up disabled).  Note pull-down resistors are NOT supported!
     */
    public byte gppuA(){
        return this.read_u8(_GPPUA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void gppuA(byte val){
        write_u8(_GPPUA, val);
    }

    /**
     * The raw GPPU B pull-up register.  Each bit represents
     * if a pull-up is enabled on the specified pin (1 = pull-up enabled,
     * 0 = pull-up disabled).  Note pull-down resistors are NOT supported!
     */
    public byte gppuB(){
        return this.read_u8(_GPPUB);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void gppuB(byte val){
        write_u8(_GPPUB, val);
    }

    /**
     * The raw IPOL output register.  Each bit represents the
     * polarity value of the associated pin (0 = normal, 1 = inverted), assuming that
     * pin has been configured as an input previously.
     */
    public int ipol(){
        return this.read_int(_IPOLA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void ipol(int val){
        write_int(_IPOLA, val);
    }

    /**
     * The raw IPOL A output register.  Each bit represents the
     * polarity value of the associated pin (0 = normal, 1 = inverted), assuming that
     * pin has been configured as an input previously.
     */
    public byte ipolA(){
        return this.read_u8(_IPOLA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void ipolA(byte val){
        write_u8(_IPOLA, val);
    }

    /**
     * The raw IPOL B output register.  Each bit represents the
     * polarity value of the associated pin (0 = normal, 1 = inverted), assuming that
     * pin has been configured as an input previously.
     */
    public byte ipolB(){
        return this.read_u8(_IPOLB);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void ipolB(byte val){
        write_u8(_IPOLB, val);
    }

    /**
     * The raw INTCON interrupt control register. The INTCON register
     * controls how the associated pin value is compared for the
     * interrupt-on-change feature. If  a  bit  is  set,  the  corresponding
     * I/O  pin  is  compared against the associated bit in the DEFVAL
     * register. If a bit value is clear, the corresponding I/O pin is
     * compared against the previous value.
     */
    public int interrupt_configuration(){
        return this.read_int(_INTCONA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void interrupt_configuration(int val){
        this.write_int(_INTCONA, val);
    }

    /**
     * The raw GPINTEN interrupt control register. The GPINTEN register
     * controls the interrupt-on-change feature for each pin. If a bit is
     * set, the corresponding pin is enabled for interrupt-on-change.
     * The DEFVAL and INTCON registers must also be configured if any pins
     * are enabled for interrupt-on-change.
     */
    public int interrupt_enable(){return this.read_int(_GPINTENA);}

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void interrupt_enable(int val){this.write_int(_GPINTENA, val);}

    /**
     * The raw DEFVAL interrupt control register. The default comparison
     * value is configured in the DEFVAL register. If enabled (via GPINTEN
     * and INTCON) to compare against the DEFVAL register, an opposite value
     * on the associated pin will cause an interrupt to occur.
     */
    public int default_value(){
        return this.read_int(_DEFVALA);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void default_value(int val){
        this.write_int(_DEFVALA, val);
    }

    /**
     * The raw IOCON configuration register. Bit 1 controls interrupt
     * polarity (1 = active-high, 0 = active-low). Bit 2 is whether irq pin
     * is open drain (1 = open drain, 0 = push-pull). Bit 3 is unused.
     * Bit 4 is whether SDA slew rate is enabled (1 = yes). Bit 5 is if I2C
     * address pointer auto-increments (1 = no). Bit 6 is whether interrupt
     * pins are internally connected (1 = yes). Bit 7 is whether registers
     * are all in one bank (1 = no), this is silently ignored if set to ``1``.
     */
    public int io_control(){
        return this.read_int(_IOCON);
    }

    /**
     * Setter
     * @param val 16bit int representing the register
     */
    public void io_control(int val){
        this.write_int(_IOCON, val);
    }

    /**
     * Returns a list with the pin numbers that caused an interrupt
     * port A ----> pins 0-7
     * port B ----> pins 8-15
     */
    public ArrayList<Integer> int_flag(){
        int intf = this.read_int(_INTFA);

        ArrayList<Integer> flags = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if((intf & (1 << i)) == 1){
                flags.add(i);
            }
        }
        return flags;
    }

    /**
     * Returns a list with the pin numbers that caused an interrupt
     * port A ----> pins 0-7
     */
    public ArrayList<Integer> int_flaga(){
        int intf = this.read_u8(_INTFA);

        ArrayList<Integer> flags = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if((intf & (1 << i)) == 1){
                flags.add(i);
            }
        }
        return flags;
    }

    /**
     * Returns a list with the pin numbers that caused an interrupt
     * port A ----> pins 8-15
     */
    public ArrayList<Integer> int_flagb(){
        int intf = this.read_u8(_INTFB);

        ArrayList<Integer> flags = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if((intf & (1 << i)) == 1){
                flags.add(i+8);
            }
        }
        return flags;
    }

    /**
     * Returns a list with the pin values at time of interrupt
     * port A ----> pins 0-7
     * port B ----> pins 8-15
     */
    public ArrayList<Integer> int_cap(){
        int intcap = this.read_int(_INTCAPA);

        ArrayList<Integer> flags = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if((intcap & (1 << i)) == 1){
                flags.add(1);
            }else{
                flags.add(0);
            }
        }
        return flags;
    }

    /**
     * Returns a list with the pin values at time of interrupt
     * port A ----> pins 0-7
     */
    public ArrayList<Integer> int_capa(){
        int intcap = this.read_u8(_INTCAPA);

        ArrayList<Integer> flags = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if((intcap & (1 << i)) == 1){
                flags.add(1);
            }else{
                flags.add(0);
            }
        }
        return flags;
    }

    /**
     * Returns a list with the pin values at time of interrupt
     * port B ----> pins 8-15
     */
    public ArrayList<Integer> int_capb(){
        int intcap = this.read_u8(_INTCAPB);

        ArrayList<Integer> flags = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if((intcap & (1 << i)) == 1){
                flags.add(1);
            }else{
                flags.add(0);
            }
        }
        return flags;
    }

    /**
     * Clears interrupts by reading INTCAP
     */
    public void clear_ints(){
        this.read_int(_INTCAPA);
    }

    /**
     * Clears interrupts by reading INTCAPA
     */
    public void clear_inta(){
        this.read_u8(_INTCAPA);
    }

    /**
     * Clears interrupts by reading INTCAPB
     */
    public void clear_intb(){
        this.read_u8(_INTCAPB);
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
        private final Mcp23017 mcp;

        public DigitalInOut(Mcp23017 mcp, int pin){
            this.mcp = mcp;
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
        public void switch_to_intput(boolean pullup, boolean invert_polarity){
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
            return get_bit(mcp.gpio(), pin);
        }

        /**
         * Setter
         * @param val setting the input
         */
        public void setValue(boolean val){
            if(val){
                this.mcp.gpio(enable_bit(this.mcp.gpio(), pin));
            }else{
                this.mcp.gpio(clear_bit(this.mcp.gpio(), pin));
            }
        }

        /**
         * The direction of the pin, either True for an input or
         * False for an output.
         */
        public boolean direction(){
            if(get_bit(this.mcp.iodir(), pin)){
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
                this.mcp.iodir(enable_bit(this.mcp.iodir(), pin));
            } else if (direction == Direction.OUTPUT) {
                this.mcp.iodir(clear_bit(this.mcp.iodir(), pin));
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
            return get_bit(this.mcp.gppu(), pin);
        }

        /**
         * Setter
         */
        public void setPullup(boolean val){
            if(val){
                this.mcp.gppu(enable_bit(this.mcp.gppu(), pin));
            }else{
                this.mcp.gppu(clear_bit(this.mcp.gppu(), pin));
            }
        }

        /**
         * The polarity of the pin, either True for an Inverted or
         * False for a normal.
         */
        public boolean invert_polarity(){
            return get_bit(this.mcp.ipol(), pin);
        }

        public void invert_polarity(boolean val){
            if(val){
                this.mcp.ipol(enable_bit(this.mcp.ipol(), pin));
            }else{
                this.mcp.ipol(clear_bit(this.mcp.ipol(), pin));
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
