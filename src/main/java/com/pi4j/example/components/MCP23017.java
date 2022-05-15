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
    private I2C i2cDevice;
    private boolean monitorIntrp;
    private int pin;
    private int busNum;
    private int address;
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
        byte regAddr[] = { 0x00, 0x02, 0x04, 0x06, 0x08, 0x0A, 0x0C, 0x0E, 0x10, 0x12, 0x14 };
        return (regAddr);
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
        byte regAddr[] = { 0x01, 0x03, 0x05, 0x07, 0x09, 0x0B, 0x0D, 0x0F, 0x11, 0x13, 0x15 };
        return (regAddr);
    }

     /**
     * PrettyPrint register values
     */
    public void dumpRegs() {
        logInfo("dump_regs ");

        String regName[] = { "_IODIR  ", "_IPOL   ", "_GPINTE ", "_DEFVAL ", "_INTCON ", "_IOCON  ", "_GPPU   ",
                "_INTF   ", "_INTCAP ", "_GPIO   ", "_OLAT   " };
        byte regAddr[] = this.getAddrMapFirst8();
        logDebug("this.getAddrMapFirst8() function returned     " + getAddrMapFirst8());

        String regNameB[] = { "_IODIRB ", "_IPOLB  ", "_GPINTEB", "_DEFVALB", "_INTCONB", "_IOCON  ", "_GPPUB  ",
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
        byte regAddrB[] = this.getAddrMapSecond8();

        logDebug("this.getAddrMapSecond8() function returned     " + getAddrMapSecond8());
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
        int b;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte first8[] = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[_IODIR];

        byte thisOffsetGPI = first8[_GPIO];
        byte thisOffsetOLA = first8[_OLAT];
        if (pin > 7) {
            absPin = absPin - 8;
            byte second8[] = this.getAddrMapSecond8();

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
            Integer integerObject = ((1 << absPin));
            b = integerObject.byteValue();
            reg = (byte)(reg | b);
        } else {
            Integer integerObject = (~(1 << absPin));
            b = integerObject.byteValue();
            reg = (byte)(reg & b);
        }

        // System.out.println("write GPI " + String.format("0x%02X", reg[0]));

        reg = reg & 0xff;
        i2cDevice.write(thisOffsetGPI, (byte) reg);

        // OLAT
        reg = i2cDevice.readRegister( thisOffsetOLA);
        if (pinOn) {
            Integer integerObject = ((1 << absPin));
            b = integerObject.byteValue();
            reg =  (reg | b);
        } else {
            Integer integerObject = (~(1 << absPin));
            b = integerObject.byteValue();
            reg = (reg & b);
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
        byte first8[] = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[_IODIR];
        byte thisOffsetGPI = first8[_GPIO];
        if (pin > 7) {
            absPin = absPin - 8;
            byte second8[] = this.getAddrMapSecond8();
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
        int intPin = gpioPin;
        if (onOff.equals("on")) {
            this.gpio.getCfgData(intPin).input.addListener((DigitalStateChangeListener) new McpBaseIntrpListener(this));
        } else if (onOff.equals("off")) {
            // cannot remove handler (yet)
        } else {
            logError("addListener: invalid onOff");
            return;
        }
    }

    public void processKeyedData() {
        logInfo("processKeyedData ");
        HashMap<String, HashMap<String, String>> outerMap = this.cfgData.getFullMap();

        // this.configUtils.confgureGpios(cfgData);
        Set outerSet = outerMap.entrySet();
        Iterator<Map.Entry<String, Map<String, String>>> outerIterator = outerSet.iterator();
        while (outerIterator.hasNext()) {
            Map.Entry<String, Map<String, String>> mentry = (Map.Entry) outerIterator.next();
            // iterate inner map
            HashMap<String, String> innerMap = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> child = (mentry.getValue()).entrySet().iterator();
            while (child.hasNext()) {
                Map.Entry<String, String> childPair = child.next();
                String key =  childPair.getKey();
                byte[] first8 = getAddrMapFirst8();
                byte regValOffset = first8[_IOCON];
                if (key.equals("act")) {
                    byte ioconReg;
                    ioconReg = (byte) i2cDevice.readRegister(regValOffset);
                    String level =  childPair.getValue();
                    if (level.contains("low")) {
                        ioconReg = (byte) (ioconReg & (~2));
                    } else if (level.contains("high")) {
                        ioconReg = (byte) (ioconReg | 2);
                    }
                    i2cDevice.write(regValOffset, ioconReg);
                }
            }
            String[] pinList = { "pin0", "pin1", "pin2", "pin3", "pin4", "pin5", "pin6", "pin7", "pin8", "pin9",
                    "pin10", "pin11", "pin12", "pin13", "pin14", "pin15" };
            for (int i = 0; i < pinList.length; i++) {
                if (outerMap.containsKey(pinList[i])) {
                    this.processPinData(i, pinList[i], outerMap.get(pinList[i]));
                }
            }
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
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
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
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
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
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            i2cDevice.write(thisOffset, reg);
        } else if (key.contains("do_compare")) {
            byte first8[] = getAddrMapFirst8();
            byte thisOffset = first8[_INTCON];
            if (pin > 7) {
                absPin = absPin - 8;
                byte second8[] = getAddrMapSecond8();
                thisOffset = second8[_INTCON];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) i2cDevice.readRegister(thisOffset );
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject;
                integerObject = (~(1 << absPin));
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            i2cDevice.write( thisOffset, reg);
        } else if (key.contains("invert")) {
            byte first8[] = getAddrMapFirst8();
            byte thisOffset = first8[_IPOL];
            if (pin > 7) {
                absPin = absPin - 8;
                byte second8[] = getAddrMapSecond8();
                thisOffset = second8[_IPOLB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte)i2cDevice.readRegister(thisOffset);
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                Integer integerObject;
                integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            i2cDevice.write(thisOffset, reg);
        } else if (key.contains("int_ena")) {
            byte first8[] = getAddrMapFirst8();
            byte thisOffset = first8[_GPINTEN];
            if (pin > 7) {
                absPin = absPin - 8;
                byte second8[] = getAddrMapSecond8();
                thisOffset = second8[_GPINTENB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) i2cDevice.readRegister( thisOffset);
            logDebug(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = (~(1 << absPin));
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
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
            byte first8[] = this.getAddrMapFirst8();

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

    public class McpBaseIntrpListener implements DigitalStateChangeListener {
        public McpBaseIntrpListener(MCP23017 chip) {
            this.chip = chip;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("McpBaseIntrpListener: Performing ctl-C shutdown");
            }));
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

public class GpioPinCfgData {

    Direction direction;
    public int number;
    public DigitalOutput output;
    public DigitalInput input;

    public enum Direction {
        in, out, none
    }

    public GpioPinCfgData(int number, Direction direction, DigitalOutput output, DigitalInput input) {

        this.number = number;
        this.output = output;
        this.input = input;
        this.direction = direction;
    }
}

public class BaseGpioInOut extends Component{

    public HashMap<Integer, GpioPinCfgData> pinDict;
    Context pi4j;

    /**
     * BaseGpioInOut CTOR
     * <p>
     * PreCond: BaseGpioInOut CTOR called with valid parameters
     * <ul>
     *     <li>Instantiated Context class
     *     <li> Instantiated FFDC class
     *     <li> HashMap GpioPin to CfgData (Gpio Device)
     * </ul>
     * <p>
     * PostCond:  Class methods are now accessable
     */
    public BaseGpioInOut(Context pi4j, HashMap<Integer, GpioPinCfgData> dioPinData) {
        this.pinDict = dioPinData;
        this.pi4j = pi4j;
        this.initPin();
    }

    /**
     * Placeholder to add initialization code
     *
     */
    private boolean initPin() {
        logInfo("BaseGpioInOut::initPin " + true);
        return (true);
    }



    /**
     * pinExists
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict return true, else false
     *      </p>
     */
    public boolean pinExists(Integer pin) {
        this.dumpHashMap();
        boolean rtn = this.pinDict.containsKey(pin);
        logInfo("BaseGpioInOut::pinExists pin " + pin + "  " + rtn);
        return (rtn);
    }

    /**
     * pinIsOutput
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict is output return true, else false
     *      </p>
     */
    public boolean pinIsOutput(Integer pin) {
        boolean rtn = false;
        if (this.pinExists(pin)) {
            GpioPinCfgData pData = this.pinDict.get(pin);
            if (pData.direction == GpioPinCfgData.Direction.out) {
                rtn = true;
            }
        }
        logInfo("BaseGpioInOut::pinIsOutput pin " + pin + "  " + rtn);
        return (rtn);
    }

    /**
     * Debug usage, dump pinDict
     */
    public void dumpHashMap() {
        // Iterate through the hashmap
        // and add some bonus marks for every student
        logDebug("HashMap pin data   this.pinDict : " + this.pinDict + "\n\n");
        Iterator<Map.Entry<Integer, GpioPinCfgData>> child = this.pinDict.entrySet().iterator();
        while (child.hasNext()) {
            Map.Entry childPair = child.next();
            logDebug("childPair.getKey() :   " + childPair.getKey() + " childPair.getValue()  :  "
                    + childPair.getValue().toString());

        }

    }

    /**
     * pinIsInput
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict is input return true, else false
     *      </p>
     */
    public boolean pinIsInput(Integer pin) {
        boolean rtn = false;
        if (this.pinExists(pin)) {
            GpioPinCfgData pData = this.pinDict.get(pin);
            if (pData.direction == GpioPinCfgData.Direction.in) {
                rtn = true;
            }
        }
        // System.out.println("pinIsInput " + rtn);
        logInfo("BaseGpioInOut::pinIsInput pin " + pin + " " + rtn);
        return (rtn);
    }

    /**
     * getCfgData
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict, data returned
     *      </p>
     * @return GpioPinCfgData
     */
    public GpioPinCfgData getCfgData(Integer pin) {
        GpioPinCfgData pData = null;
        if (this.pinExists(pin)) {
            pData = this.pinDict.get(pin);
        }
        logInfo("BaseGpioInOut::getCfgData pin " + pin + " " + pData);
        return (pData);
    }

    /**
     * addPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     * @param data   GpioPinCfgData associated data
     *
     *     <p>
     *      PostCond:  If pin in pinDict, data returned
     *      </p>
     */
    public void addPin(Integer pin, GpioPinCfgData data) {
        logInfo("BaseGpioInOut::addPin pin " + pin + "  data" + data);
        this.dumpHashMap();
        this.pinDict.put(pin, data);
        this.dumpHashMap();
    }

    /**
     * createInPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param name config id
     * @param number  Pin number,config address
     * @param pullup  config pull resistance
     *     <p>
     *      PostCond:  Pin device created, and  pin in pinDict
     *      </p>
     * @return if successful true else false
     */
    public boolean createInPin(String name, Integer number, PullResistance pullup) {
        boolean success = false;
        logInfo("BaseGpioInOut::createInPin  " + name + "pin " + number + "  up/dwn " + pullup);
        if (this.pinExists(number)) {
            logDebug(" Pin  " + number + " already in map : " + this.getCfgData(number));
        } else {
            logDebug("create inpin  " + name);
            var ledConfig = DigitalInput.newConfigBuilder(this.pi4j)
                    .id(name)
                    .name(name)
                    .address(number)
                    .pull(pullup)
                    .provider("pigpio-digital-input");
            DigitalInput input = null;
            try {
                input = this.pi4j.create(ledConfig);
            } catch (Exception e) {
                e.printStackTrace();
                logError("create DigIn failed");
                return Boolean.FALSE;
            }

            GpioPinCfgData pData = new GpioPinCfgData(number, GpioPinCfgData.Direction.in, null, input);
            this.addPin(number, pData);
            success = true;
            logDebug("pData :" + pData);
        }
        this.dumpHashMap();
        return (success);
    }


    /**
     * createOutPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param name config id
     * @param number  Pin number,config address
     * @param initialValue  config initial
     *     <p>
     *      PostCond:  Pin device created, and  pin in pinDict
     *      </p>
     * @return if successful true else false
     */
    public boolean createOutPin(String name, Integer number, DigitalState initialValue) {
        boolean success = false;
        logInfo("BaseGpioInOut::createOutPin  " + name + " pin " + number + "  state " + initialValue);
        this.dumpHashMap();
        if (this.pinExists(number)) {
            logDebug(" Pin  " + number + " already in map : " + this.getCfgData(number));
        } else {
            System.out.println("create outpin  " + name);
            var ledConfig = DigitalOutput.newConfigBuilder(this.pi4j)
                    .id(name)
                    .name(name)
                    .address(number)
                    .shutdown(initialValue)
                    .initial(initialValue)
                    .provider("pigpio-digital-output");
            DigitalOutput output = null;
            try {
                output = this.pi4j.create(ledConfig);
            } catch (Exception e) {
                e.printStackTrace();
                logInfo("create DigOut failed");
                logError("create DigOut failed");
            }
            GpioPinCfgData pData = new GpioPinCfgData(number, GpioPinCfgData.Direction.out, output, null);
            this.addPin(number, pData);
            success = true;
            logDebug("pData :" + pData);
        }
        this.dumpHashMap();
        logError("BaseGpioInOut::createOutPin success " + success);
        return (success);
    }

    /**
     * drivePinHigh
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number  Pin number
     *     <p>
     *      PostCond:  Pin driven high
     *      </p>
     */
    public void drivePinHigh(Integer number) {
        logInfo("BaseGpioInOut::drivePinHigh pin " + number);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            try {
                pData.output.high();
            } catch (com.pi4j.io.exception.IOException e) {
                e.printStackTrace();
            }
        } else {
            logWarning("Invalid usage for pin direction");
            logError("Invalid pin direction");
        }
    }

    /**
     * drivePinLow
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number Pin number
     *     <p>
     *      PostCond:  Pin driven low
     *      </p>
     */
    public void drivePinLow(Integer number) {
        logInfo("BaseGpioInOut::drivePinLow pin " + number);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            try {
                pData.output.low();
            } catch (com.pi4j.io.exception.IOException e) {
                e.printStackTrace();
            }
        } else {
            logError("Invalid usage for pin direction");
        }
    }

    /**
     * togglePin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number Pin number
     *     <p>
     *      PostCond:  Pin toggled
     *      </p>
     */
    public void togglePin(Integer number) {
        logInfo("BaseGpioInOut::togglePin pin " + number);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            try {
                pData.output.toggle();
            } catch (com.pi4j.io.exception.IOException e) {
                e.printStackTrace();
            }
        } else {
            logError("Invalid usage for pin direction");
        }
    }

    /**
     * pulse
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number  Pin number
     * @parm count  pulse count
     *     <p>
     *      PostCond:  Pin pulsed
     *      </p>
     */
    public void pulse(Integer number, long count) {
        logInfo("BaseGpioInOut::pulse pin " + number + " count" + count);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            pData.output.pulse(number, TimeUnit.MICROSECONDS);
        } else {
            logError("Invalid usage for pin direction");
        }
    }

    /**
     * readPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number Pin number
     *     <p>
     *      PostCond:  Pin read if exists, else return  DigitalState.UNKNOWN
     *         </p>
     * @return  DigitalState
     */
    public DigitalState readPin(Integer number) {
        logInfo("BaseGpioInOut::readPin pin " + number);
        DigitalState rtnVal = DigitalState.UNKNOWN;
        if (pinIsInput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            rtnVal = pData.input.state();
        } else {
            logError("Invalid usage for pin direction");
            return null;
        }
        return (rtnVal);
    }

    /**
     * createGpioInstance
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param  pinCfgMap  Dictionary describing Gpio pins
     *   "{{'gpio24':{'name':'RedLED','dir':'out','initial':'high'}},
     *     {'gpio20':{'name':'sensor','dir':'in','pull':up'}}}"
     *
     *     <p>
     *      PostCond:  Pins created
     *         </p>
     * @return  if successful true else false
     */
    public boolean createGpioInstance(HashMap<String, HashMap<String, String>> pinCfgMap) {
        logInfo("BaseGpioInOut::createGpioInstance");
        boolean rval =  false;
        HashMap<String, HashMap<String, String>> outerMap = pinCfgMap;
        Set outerSet = outerMap.entrySet();
        Iterator<Map.Entry<String, Map<String, String>>> outerIterator = outerSet.iterator();
        while (outerIterator.hasNext()) {
            Map.Entry<String, Map<String, String>> mentry = (Map.Entry) outerIterator.next();
            System.out.println("mentry  " + mentry);
            String pinName = mentry.getKey();
            if (pinName.startsWith("gpio") == false) {
                logError("illegal name prefix :" + pinName);
                return Boolean.FALSE;
            }
            int pinNumber = Integer.parseInt(pinName.substring(4));
            int pin = pinNumber;

            // iterate inner map
            Map<String, String> innerMap = mentry.getValue();

            String dir = innerMap.get("dir");
            String name = innerMap.get("name");

            if (dir.equals("in")) {
                String pull = innerMap.get("pull");
                if (pull.equals("up")) {
                    rval =this.createInPin(name, pin, PullResistance.PULL_UP);
                } else {
                    rval = this.createInPin(name, pin, PullResistance.PULL_DOWN);
                }
            } else { // out direction
                String initial = innerMap.get("initial");
                if (initial.equals("high")) {
                    rval =this.createOutPin(name, pin,DigitalState.HIGH);
                } else {
                    rval = this.createOutPin(name, pin, DigitalState.LOW);
                }
            }
        }
        this.dumpHashMap();
        return (rval);
    }

    public void resetChip(int resetGpio, Context pi4j, int delay, boolean bar) {
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("resetPin")
                .name("Chip reset")
                .address(resetGpio)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        DigitalOutput resetPin = null;
        try {
            resetPin = pi4j.create(ledConfig);
        } catch (Exception e) {
            e.printStackTrace();
            logError(String.format("reset_chip  %s" ,e.toString()));
            return;
        }
        if(bar) {  // active low
            resetPin.low();
            delay(delay);
            resetPin.high();
        }else{
            resetPin.high();
            delay(delay);
            resetPin.low();
        }
    }
}