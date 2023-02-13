package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import java.util.*;

public class MCP23017 extends Component{
    /**
     * The Default BUS and Device Address.
     * On the PI, you can look it up with the Command 'sudo i2cdetect -y 1'
     */
    private static final int DEFAULT_BUS = 0x1;
    private static final int DEFAULT_DEVICE = 0x27;

    private String mainChip;
    private boolean dumpRegs;
    private boolean readPin;
    private boolean pinOn;
    private boolean setPin;
    private String fullKeyedData;
    private String fullPinKeyedData;
    private boolean hasFullKeyedData;
    private boolean hasFullPinKeyedData;

    private boolean hasIOCONKeyedData;
    private String IOCONKeyedData;

    private String priChipName;
    private String pinName;
    private int priChipBusNum;
    private int priChipAddress;

    private boolean doReset;

    private byte configInfo;
    private byte intfA;
    private byte intfB;
    private int gpioNum;
    private String offOn;
    private String upDown;
    private int intrptCount;
    private int gpioReset;
    private boolean banked;
    private boolean bankCapable;

    private final Context pi4j;
    private final I2C i2cDevice;
    private boolean monitorIntrp;
    private int pin;
    private final int busNum;
    private final int address;
    public HashMap<Integer, GpioPinCfgData> dioPinData;
    public BaseGpioInOut gpio;
    private GpioPinCfgData cfgData;

    int _IODIR = 0x00;
    int _IPOL = 0x01;
    int _GPINTEN = 0x02;
    int _DEFVAL = 0x03;
    int _INTCON = 0x04;
    int _IOCON = 0x05;
    int _GPPU = 0x06;
    int _INTF = 0x07;
    int _INTCAP = 0x08;
    int _GPIO = 0x09;
    int _OLAT = 0x0A;

    int _IODIRB = 0x00;
    int _IPOLB = 0x01;
    int _GPINTENB = 0x02;
    int _DEFVALB = 0x03;
    int _INTCONB = 0x04;
    int _IOCONB = 0x05;
    int _GPPUB = 0x06;
    int _INTFB = 0x07;
    int _INTCAPB = 0x08;
    int _GPIOB = 0x09;
    int _OLATB = 0x0A;

    /**
     * CTOR
     * @param pi4j        Context
     * @param dioPinData    Pi Gpio config devices
     */
    public MCP23017(Context pi4j, HashMap<Integer, GpioPinCfgData> dioPinData) {
        this(pi4j, dioPinData, DEFAULT_BUS, DEFAULT_DEVICE);
    }

    /**
     * CTOR
     * @param pi4j        Context
     * @param dioPinData    Pi Gpio config devices
     */
    public MCP23017(Context pi4j, HashMap<Integer, GpioPinCfgData> dioPinData, int bus, int device) {
        this.pi4j = pi4j;
        this.dioPinData = dioPinData;
        this.busNum = bus;
        this.address = device;
        this.i2cDevice = pi4j.create(buildI2CConfig(pi4j, bus, device));
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
     *
     * @return    Array of register addresses, offset defined by McpConfigData
     * <p>
     *     Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapFirst8() {
        logInfo("23017 getAddrMapFirst8");
        return (new byte[]{ 0x00, 0x02, 0x04, 0x06, 0x08, 0x0A, 0x0C, 0x0E, 0x10, 0x12, 0x14 });
    }

    /**
     *
     * @return    Array of register addresses, offset defined by McpConfigData
     * <p>
     *     Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapSecond8() {
        logInfo("23017 getAddrMapSecond8");
        return (new byte[]{ 0x01, 0x03, 0x05, 0x07, 0x09, 0x0B, 0x0D, 0x0F, 0x11, 0x13, 0x15 });
    }

     /**
     * PrettyPrint register values
     */
    public void dumpRegs() {
        logInfo("dump_regs ");

        String[] regName = { "_IODIR  ", "_IPOL   ", "_GPINTE ", "_DEFVAL ", "_INTCON ", "_IOCON  ", "_GPPU   ",
                "_INTF   ", "_INTCAP ", "_GPIO   ", "_OLAT   " };
        byte[] regAddr = this.getAddrMapFirst8();
        logDebug("this.getAddrMapFirst8() function returned     " + Arrays.toString(getAddrMapFirst8()));

        String[] regNameB = { "_IODIRB ", "_IPOLB  ", "_GPINTEB", "_DEFVALB", "_INTCONB", "_IOCON  ", "_GPPUB  ",
                "_INTFB  ", "_INTCAPB", "_GPIOB  ", "_OLATB  " };
        String[][] pinName = { { "IO7    IO6    IO5    IO4    IO3    IO2    IO1    IO0" },
                { "IP7    IP6    IP5    IP4    IP3    IP2    IP1    IP0   " },
                { "GPINT7 GPINT6 GPINT5 GPINT4 GPINT3 GPINT2 GPINT1 GPINT0  " },
                { "DEF7   DEF6   DEF5   DEF4   DEF3   DEF2   DEF1   DEF0    " },
                { "IOC7   IOC6   IOC5   IOC4   IOC3   IOC2   IOC1   IOC0    " },
                { "BANK   MIRROR SEQOP  DISSLW HAEN   ODR    INTPOL     " },
                { "PU7    PU6    PU5    PU4    PU3    PU2    PU1    PU0     " },
                { "INT7   INT6   INT5   INT4   INT3   INT2   INT1   INT0    " },
                { "ICP7   ICP6   ICP5   ICP4   ICP3   ICP2   ICP1   ICP0    " },
                { "GP7    GP6    GP5    GP4    GP3    GP2    GP1    GP0     " },
                { "OL7    OL6    OL5    OL4    OL3    OL2    OL1    OL0     " }, };
        byte[] regAddrB = this.getAddrMapSecond8();

        logDebug("this.getAddrMapSecond8() function returned     " + Arrays.toString(getAddrMapSecond8()));
        String regAstr = "";
        int reg ;

        for (int i = 0; i < regName.length; i++) {
            reg = i2cDevice.readRegister(regAddr[i]);
            regAstr = regAstr.concat("\n   Reg " + regName[i] + " offset ("+ i + ")  data: "  +   String.format("0x%02X", reg) + "\n");
            int val =  reg;

            regAstr = regAstr.concat(pinName[i][0] + "\n");
            regAstr = regAstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                    + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                    + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n");
        }
        logDebug(regAstr);
        if (this.bankCapable) {
            delay(1000);
            String regBstr = "";
            int regB;

            for (int i = 0; i < regNameB.length; i++) {
                regB = i2cDevice.readRegister(regAddrB[i]);
                regBstr = regBstr.concat("\n   RegB " + regNameB[i] + " offset ("+ i + ")  data: "  +   String.format("0x%02X", regB) + "\n");
                int val =  regB;

                regBstr = regBstr.concat(pinName[i][0] + "\n");
                regBstr = regBstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                        + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3)
                        + "      " + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01))
                        + "\n");
            }
           logDebug(regBstr);
        }
    }

    /**
     *
     * @param pin         MCP pin to drive
     * @param pinOn       If true drive HIGH, else drive LOW
     */
    public void drivePin(int pin, boolean pinOn){
        // get the regs and make sure the desired pin is configed as output. Log
        // error if not
        int configed;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte[] first8 = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[_IODIR];

        byte thisOffsetGPI = first8[_GPIO];
        byte thisOffsetOLA = first8[_OLAT];
        if (pin > 7) {
            absPin = absPin - 8;
            byte[] second8 = this.getAddrMapSecond8();

            thisOffsetIOD = second8[_IODIRB];
            thisOffsetGPI = second8[_GPIOB];
            thisOffsetOLA = second8[_OLATB];
        }

        logInfo("drivePin  pin" + String.format("0x%02X", pin) + " pinOn : " + pinOn);

        configed = i2cDevice.readRegister(thisOffsetIOD);

        if ((configed & (1 << absPin)) > 0) {
            System.out.println("Pin" + pin + "  not configured for output");
            System.exit(500);
        }
        reg = i2cDevice.readRegister( thisOffsetGPI);
        // System.out.println("read GPI " + String.format("0x%02X", reg[0]));
        if (pinOn) {
            int integerObject = ((1 << absPin));
            reg = (byte)(reg | integerObject);
        } else {
            int integerObject = (~(1 << absPin));
            reg = (byte)(reg & integerObject);
        }

        // System.out.println("write GPI " + String.format("0x%02X", reg[0]));

        reg = reg & 0xff;
        i2cDevice.write(thisOffsetGPI, (byte) reg);

        // OLAT
        reg = i2cDevice.readRegister( thisOffsetOLA);
        if (pinOn) {
            int integerObject = ((1 << absPin));
            reg =  (reg | integerObject);
        } else {
            int integerObject = (~(1 << absPin));
            reg = (reg & integerObject);
        }
        reg = reg & 0xff;

        i2cDevice.write(thisOffsetOLA, (byte) reg);

        logInfo("drivePin");
    }

    /**
     *
     * @param pin    MCP pin to read
     *               <p>
     *                Pin read and detail logged.
     *               </p>
     */
    public void readInput(int pin){
        // # get the regs and make sure the desired pin is configed as input.
        // Log error if not
        int configed;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte[] first8 = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[_IODIR];
        byte thisOffsetGPI = first8[_GPIO];
        if (pin > 7) {
            absPin = absPin - 8;
            byte[] second8 = this.getAddrMapSecond8();
            thisOffsetIOD = second8[_IODIRB];
            thisOffsetGPI = second8[_GPIOB];
        }

        logInfo(" readInput  pin " + String.format("0x%02X", pin));

        configed = i2cDevice.readRegister(thisOffsetIOD);

        if ((configed & (1 << absPin)) == 0) {
            logWarning("Pin" + String.format("0x%02X", pin) + "  not configured for input");
            logError("Incorrect Pin direction");
            return;
        }

        reg = i2cDevice.readRegister( thisOffsetGPI);
        if ((reg & (1 << absPin)) == 0) {
            System.out.println("Pin" + pin + " Low");
            logDebug("Pin" + pin + " Low");
        } else {
            System.out.println("Pin" + pin + " High");
            logDebug("Pin" + pin + " High");
        }
    }


    /**
     *
     * @param onOff   Enable listener if true, else (at present disable not possible)
     * @param gpioPin  Pi GPIO to apply the listener
     * These listen to pi DIO interrupts. If on occurs this pgm handler is
     *  called. The handler then
     *  interrogates the 23xxx chip to see which of its pins created the
     *  interrupt.
     *                 <P>
     *                 PreCond:  BaseGpioInOut contains a relevant GpioPinCfgData.
     *                 This data supplied by the program user must include gpioPin
     *                 as Input and the needed pullDown value
     *                 </P>
     */
    public void addListener(String onOff, int gpioPin) {
        // find BCM number for this pin
        logInfo(" addListener onOff : " + onOff + "  pin" + gpioPin);
        if (onOff.equals("on")) {
            this.gpio.getCfgData(gpioPin).input.addListener((DigitalStateChangeListener) new McpBaseIntrpListener(this));
        } else if (onOff.equals("off")) {
            // cannot remove handler (yet)
        } else {
            logError("addListener: invalid onOff");
        }
    }

    public void processPinData(int pin_num, String pin, HashMap<String, String> innerData) {
        // this.cfgData;
        logInfo("processPinData  pinNum : " + String.format("0x%02X", pin_num) + "  " + pin
                + "  innerData : " + innerData);
        String value;
        String[] opt_list = { "dir", "pull", "default", "do_compare", "int_ena" };
        for (int c = 0; c < opt_list.length; c++) {
            // System.out.println("pin_list[i] " + pin_list[i] + " \n outerMap "
            // + outerMap);
            if (innerData.containsKey(opt_list[c])) {
                value = innerData.get(opt_list[c]);
                this.processOptData(pin_num, opt_list[c], value);
            }
        }
    }

    public void processOptData(int pin, String key, String value){
        logInfo(" processOptData  pin " + pin + "  key " + key + "  value " + value);
        byte reg;
        byte b;
        int absPin = pin; // if in second bank must subtract 8
        if (key.contains("dir")) {
            byte[] first8 = getAddrMapFirst8();
            byte thisOffset = first8[_IODIR];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = getAddrMapSecond8();
                thisOffset = second8[_IODIRB];
            }

            // # read return a list, get the single entry [0]
            logDebug(" I2cDevice on bus  " + this.busNum + "   Chip address :   "
                    + String.format("0x%02X", this.address) + " offset  " + thisOffset);

            reg = (byte) i2cDevice.readRegister(thisOffset);
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("in")) {
                int integerObject = (1 << absPin);
                reg = (byte) (reg | integerObject);
            } else {
                int integerObject = ~(1 << absPin);
                reg = (byte) (reg & integerObject);
            }
            i2cDevice.write(thisOffset, reg);
        } else if (key.contains("pull")) {
            byte[] first8 = getAddrMapFirst8();
            byte thisOffset = first8[_GPPU];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = getAddrMapSecond8();
                thisOffset = second8[_GPPUB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) i2cDevice.readRegister( thisOffset);
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("up")) {
                int integerObject = (1 << absPin);
                b = (byte) integerObject;
                reg = (byte) (reg | b);
            } else {
                int integerObject = ~(1 << absPin);
                b = (byte) integerObject;
                reg = (byte) (reg & b);
            }
            i2cDevice.write( thisOffset, reg);
        } else if (key.contains("default")) {
            byte[] first8 = getAddrMapFirst8();
            byte thisOffset = first8[_DEFVAL];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = getAddrMapSecond8();
                thisOffset = second8[_DEFVALB];
                // thisOffset = this.cfgData._DEFVALB;
            }
            // # read return a list, get the single entry [0]
            reg = (byte) i2cDevice.readRegister(thisOffset);
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("1")) {
                int integerObject = (1 << absPin);
                reg = (byte) (reg | integerObject);
            } else {
                int integerObject = ~(1 << absPin);
                reg = (byte) (reg & integerObject);
            }
            i2cDevice.write(thisOffset, reg);
        } else if (key.contains("do_compare")) {
            byte[] first8 = getAddrMapFirst8();
            byte thisOffset = first8[_INTCON];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = getAddrMapSecond8();
                thisOffset = second8[_INTCON];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) i2cDevice.readRegister(thisOffset );
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                int integerObject = (1 << absPin);
                reg = (byte) (reg | integerObject);
            } else {
                int integerObject;
                integerObject = (~(1 << absPin));
                reg = (byte) (reg & integerObject);
            }
            i2cDevice.write( thisOffset, reg);
        } else if (key.contains("invert")) {
            byte[] first8 = getAddrMapFirst8();
            byte thisOffset = first8[_IPOL];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = getAddrMapSecond8();
                thisOffset = second8[_IPOLB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte)i2cDevice.readRegister(thisOffset);
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                int integerObject;
                integerObject = (1 << absPin);
                reg = (byte) (reg | integerObject);
            } else {
                int integerObject = ~(1 << absPin);
                reg = (byte) (reg & integerObject);
            }
            i2cDevice.write(thisOffset, reg);
        } else if (key.contains("int_ena")) {
            byte[] first8 = getAddrMapFirst8();
            byte thisOffset = first8[_GPINTEN];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = getAddrMapSecond8();
                thisOffset = second8[_GPINTENB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) i2cDevice.readRegister( thisOffset);
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                int integerObject = (1 << absPin);
                reg = (byte) (reg | integerObject);
            } else {
                int integerObject = (~(1 << absPin));
                reg = (byte) (reg & integerObject);
            }
            i2cDevice.write(thisOffset, reg);
        }
    }

    /**
     *  <p>
     *
     *  </p>When a Pi gpio detects a level change, if a Listener is attached, this
     *  method is called.
     *  </p>
     *  <p>
     *      The method will examine registers in the MCP chip to determine which
     *      pin changed and caused the interrupt. When the interrupting pin is
     *      determined, the processPinInterrupt method in a subclasss is called.
     *  </p>
     *
     *
     * @param event     DigitalStateChangeEvent
     */
    public void intrpHappened(DigitalStateChangeEvent event) {
        // System.out.println("intrpHappened");
        logDebug(" intrpHappened : GPIO PIN STATE CHANGE: " + event.state());// figure
        // out, which, pins // interrupted
        int reg;
        boolean foundIntrBit = false;
        int pinNum = 0;
        int testVal = 0;
        DigitalState effectedPinState = DigitalState.HIGH;
        if (this.pin < 8) {
            byte[] first8 = this.getAddrMapFirst8();

            reg = i2cDevice.readRegister(first8[_INTF]);
            // find the bit (pin) that interrupted this time.
            testVal = reg;
            testVal = testVal & 0xff;
            pinNum = 0;
            logDebug("A reg INTF " + String.format("0x%02X", reg));

            logDebug("Pin 0 - 7, inspect _INTCAP ");
            for (int c = 0; c < 8; c++) {
                if ((testVal >> c) == 1) {
                    pinNum = c;
                    foundIntrBit = true;
                    // get pin state for c + *. Set effectedPinState
                    reg =  i2cDevice.readRegister( first8[_INTCAP]);
                    testVal = reg;
                    testVal = testVal & 0xff;
                    if ((testVal >> c) == 1) {
                        effectedPinState = DigitalState.HIGH;
                    } else {
                        effectedPinState = DigitalState.LOW;
                    }
                    logDebug("A reg interrupt on pin_Nm :" + pinNum);
                    break;
                }
            }
        } else {
            if (pin > 7) { // search B bank
                logDebug("Pin 8 - 15, inspect _INTCAPB ");
                byte[] second8 = this.getAddrMapSecond8();

                reg = i2cDevice.readRegister( second8[_INTFB]);
                // find the bit (pin) that interrupted this time.
                testVal = reg;
                testVal = testVal & 0xff;
                pinNum = 0;
                logDebug("B reg " + String.format("0x%02X", reg));
                for (int c = 0; c < 8; c++) {
                    if ((testVal >> c) == 1) {
                        pinNum = c + 8;
                        foundIntrBit = true;
                        // get pin state for c + *. Set effectedPinState
                        reg = i2cDevice.readRegister(second8[_INTCAPB]);
                        testVal = reg;
                        testVal = testVal & 0xff;
                        if ((testVal >> c) == 1) {
                            effectedPinState = DigitalState.HIGH;
                        } else {
                            effectedPinState = DigitalState.LOW;
                        }
                        logDebug("B reg interrupt on  pinNum : " + pinNum);
                        break;
                    }
                }

            }
        } // pin 8-15

        if (!foundIntrBit) {
            logDebug("Bit not found in _INTF(B) ");
        } else {

            String[] pinList = { "pin0", "pin1", "pin2", "pin3", "pin4", "pin5", "pin6", "pin7", "pin8", "pin9",
                    "pin10", "pin11", "pin12", "pin13", "pin14", "pin15" };
            if (event.state() == DigitalState.LOW) {
                logDebug("    McpBase  GPIO " + pinNum + " LOW, chip pin: " + pinList[pinNum]
                        + " State: " + effectedPinState);
            } else {
                logDebug("    McpBase  GPIO " + pinNum + " HIGH, chip pin: " + pinList[pinNum]
                        + " State: " + effectedPinState);
            }
            if (this.pin == pinNum) {
                this.processPinInterrupt(pinNum, effectedPinState);
            } else {
                logDebug("    McpBase  effected pin: " + pinNum + " is not our monitored pin: " + this.pin);
            }
            this.intrptCount++;
            logDebug("Interrupt occured " + "  interrupt count : " + String.format("0x%02X", this.intrptCount));
        }
        logInfo(" intrpHappened");
    }

    /**
     * <p>
     *     The  McpBase subclass installed event handler will have control.
     * </p>
     * @param pinNum     MVP pin causing the interrupt
     * @param pinState   Pi Gpio pin state detected
     * @return  true if interrupt processed, false if failed
     */
    public boolean processPinInterrupt(int pinNum, DigitalState pinState) {
        boolean rval = false;
        logInfo("BaseClass processPinInterrupt PIN " + pinNum);// figure
        return (rval);
    }

    public Context getPi4j() {
        return pi4j;
    }

    public static class McpBaseIntrpListener implements DigitalStateChangeListener {
        public McpBaseIntrpListener(MCP23017 chip) {
            this.chip = chip;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("McpBaseIntrpListener: Performing ctl-C shutdown")));
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.LOW) {
                this.chip.intrpHappened(event);
            }
        }
        MCP23017 chip;
    }

}

