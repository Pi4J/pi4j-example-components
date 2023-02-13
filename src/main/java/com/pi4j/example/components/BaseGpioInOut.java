package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BaseGpioInOut extends Component {

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
     * @param pin <p>
     *            PostCond:  If pin in pinDict return true, else false
     *            </p>
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
     * @param pin <p>
     *            PostCond:  If pin in pinDict is output return true, else false
     *            </p>
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
     * @param pin <p>
     *            PostCond:  If pin in pinDict is input return true, else false
     *            </p>
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
     * @param pin <p>
     *            PostCond:  If pin in pinDict, data returned
     *            </p>
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
     * @param data GpioPinCfgData associated data
     *
     *             <p>
     *             PostCond:  If pin in pinDict, data returned
     *             </p>
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
     * @param name   config id
     * @param number Pin number,config address
     * @param pullup config pull resistance
     *               <p>
     *               PostCond:  Pin device created, and  pin in pinDict
     *               </p>
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
     * @param name         config id
     * @param number       Pin number,config address
     * @param initialValue config initial
     *                     <p>
     *                     PostCond:  Pin device created, and  pin in pinDict
     *                     </p>
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
     * @param number Pin number
     *               <p>
     *               PostCond:  Pin driven high
     *               </p>
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
     *               <p>
     *               PostCond:  Pin driven low
     *               </p>
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
     *               <p>
     *               PostCond:  Pin toggled
     *               </p>
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
     * @param number Pin number
     * @parm count  pulse count
     * <p>
     * PostCond:  Pin pulsed
     * </p>
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
     *               <p>
     *               PostCond:  Pin read if exists, else return  DigitalState.UNKNOWN
     *               </p>
     * @return DigitalState
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
     * @param pinCfgMap Dictionary describing Gpio pins
     *                  "{{'gpio24':{'name':'RedLED','dir':'out','initial':'high'}},
     *                  {'gpio20':{'name':'sensor','dir':'in','pull':up'}}}"
     *
     *                  <p>
     *                  PostCond:  Pins created
     *                  </p>
     * @return if successful true else false
     */
    public boolean createGpioInstance(HashMap<String, HashMap<String, String>> pinCfgMap) {
        logInfo("BaseGpioInOut::createGpioInstance");
        boolean rval = false;
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
                    rval = this.createInPin(name, pin, PullResistance.PULL_UP);
                } else {
                    rval = this.createInPin(name, pin, PullResistance.PULL_DOWN);
                }
            } else { // out direction
                String initial = innerMap.get("initial");
                if (initial.equals("high")) {
                    rval = this.createOutPin(name, pin, DigitalState.HIGH);
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
            logError(String.format("reset_chip  %s", e.toString()));
            return;
        }
        if (bar) {  // active low
            resetPin.low();
            delay(delay);
            resetPin.high();
        } else {
            resetPin.high();
            delay(delay);
            resetPin.low();
        }
    }
}
