package com.pi4j.example.components;

import com.pi4j.config.exception.ConfigException;
import com.pi4j.context.Context;
import com.pi4j.example.helpers.ContiniousMeasuringException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.RunnableFuture;

public class ADS1115 extends Component {
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
     * default bus number
     */
    private final int i2cDefaultBus = 0x01;

    /**
     * device address
     */
    private final ADDRESS address;

    /**
     * Operational status or single-shot conversion start
     */
    private int os;

    /**
     * programmable gain amplifier
     */
    private final ADS1115.GAIN pga;

    /**
     * sampling rate of device
     */

    private final DR dr;

    /**
     * Comparator mode
     */
    private final int compMode;

    /**
     * Comparator polarity
     */
    private final int compPol;

    /**
     * Latching comparator
     */
    private final int compLat;

    /**
     * Comparator queue and disable
     */
    private final int compQue;

    /**

     * actual value form conversion register (raw data)
     */
    private int[] actualValue = new int[4];
    /**
     * old value from last successful read of conversion register (raw data)
     */
    private int[] oldValue = new int[4];

    /**
     * number of channels controlled by ad converter
     */
    private final int numberOfChannels;
    /**
     * continious reading active
     */
    protected boolean continiousReadingActive;

    /**
     * The Conversion register contains the result of the last conversion.
     */
    private static final int CONVERSION_REGISTER = 0x00;
    /**
     * The Config register is used to change the ADS1115 operating modes and query the status of the device.
     */
    private static final int CONFIG_REGISTER = 0x01;
    /**
     * Lo_thresh set the lower threshold values used for the comparator function
     */
    private static final int LO_THRESH_REGISTER = 0x02;
    /**
     * Hi_thresh set the high threshold values used for the comparator function
     */
    private static final int HI_THRESH_REGISTER = 0x03;

    /**
     * Runnable code when current value from fast read is changed
     */
    private Runnable runnableFastRead;

    /**
     * Runnable code when current value from slow read is changed
     */
    private Runnable[] runnableSlowRead;

    /**
     * Config register default configuration
     */
    private int CONFIG_REGISTER_TEMPLATE;


    /**
     * Creates a new AD converter component with custom bus, device address
     *
     * @param pi4j    Pi4J context
     * @param bus     Custom I2C bus address
     * @param gain    Custom gain amplifier
     * @param address Custom device address on I2C
     */
    public ADS1115(Context pi4j, int bus, GAIN gain, ADDRESS address, int numberOfChannels) {
        this.context = pi4j;
        this.numberOfChannels = numberOfChannels;
        this.runnableSlowRead = new Runnable[numberOfChannels];

        //write default configuration
        this.os = OS.WRITE_START.getOs();
        //mux will be set in function
        this.pga = gain;
        //mode will be set in function
        this.dr = DR.SPS_128;
        this.compMode = COMP_MODE.TRAD_COMP.getCompMode();
        this.compPol = COMP_POL.ACTIVE_LOW.getCompPol();
        this.compLat = COMP_LAT.NON_LATCH.getLatching();
        this.compQue = COMP_QUE.DISABLE_COMP.getCompQue();
        createTemplateConfiguration();

        //i2c parameter
        this.deviceId = "ADS1115";
        this.i2cBus = bus;
        this.address = address;
        this.i2c = pi4j.create(buildI2CConfig(pi4j, bus, address.getAddress(), deviceId));
    }

    /**
     * Creates a new AD converter component with default bus 0x01, device bus address 0x48 (GND)
     * and max gain 4.096 V (parameters for raspberry pi)
     *
     * @param pi4j Pi4J context
     */
    public ADS1115(Context pi4j) {
        this.context = pi4j;
        this.numberOfChannels = 1;
        this.runnableSlowRead = new Runnable[1];

        //write default configuration
        this.os = OS.WRITE_START.getOs();
        //mux will be set in function
        this.pga = GAIN.GAIN_4_096V;
        //mode will be set in function
        this.dr = DR.SPS_128;
        this.compMode = COMP_MODE.TRAD_COMP.getCompMode();
        this.compPol = COMP_POL.ACTIVE_LOW.getCompPol();
        this.compLat = COMP_LAT.NON_LATCH.getLatching();
        this.compQue = COMP_QUE.DISABLE_COMP.getCompQue();
        createTemplateConfiguration();

        //i2c parameter
        this.deviceId = "ADS1115";
        this.i2cBus = i2cDefaultBus;
        this.address = ADDRESS.GND;
        this.i2c = pi4j.create(buildI2CConfig(pi4j, i2cDefaultBus, address.getAddress(), deviceId));
    }

    /**
     * read last stored conversion from device
     *
     * @return last stored conversion
     */
    public int readConversionRegister() {
        return i2c.readRegisterWord(CONVERSION_REGISTER);
    }

    /**
     * read lower threshold from device
     *
     * @return lower threshold
     */
    public int readLoThreshRegister() {
        return i2c.readRegisterWord(LO_THRESH_REGISTER);
    }

    /**
     * read upper threshold from device
     *
     * @return upper thershold
     */
    public int readHiThreshRegister() {
        return i2c.readRegisterWord(HI_THRESH_REGISTER);
    }

    /**
     * write custom configuration to device
     *
     * @param config custom configuration
     */
    public int writeConfigRegister(int config) {
        //logInfo("start write configuration");
        i2c.writeRegisterWord(CONFIG_REGISTER, config);
        //wait until ad converter has stored new value in conversion register
        //delay time is reciprocal of 1/2 of sampling time (*1000 from s to ms)
        //round value up to wait long enough
        delay((long) Math.ceil(2000.0 / dr.getSpS()));

        return readConfigRegister();
    }

    /**
     * Returns voltage value from AIn0
     *
     * @return double voltage
     */
    public double singleShotAIn0() {
        if (continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring active");
        return pga.gainPerBit * readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN0_GND.getMux() | MODE.SINGLE.getMode());
    }

    /**
     * Returns voltage value from AIn1
     *
     * @return double voltage
     */
    public double singleShotAIn1() {
        if (continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring active");
        return pga.gainPerBit * readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN1_GND.getMux() | MODE.SINGLE.getMode());
    }

    /**
     * Returns voltage value from AIn2
     *
     * @return double voltage
     */
    public double singleShotAIn2() {
        if (continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring active");
        return pga.gainPerBit * readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN2_GND.getMux() | MODE.SINGLE.getMode());
    }

    /**
     * Returns voltage value from AIn3
     *
     * @return double voltage
     */
    public double singleShotAIn3() {
        if (continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring active");
        return pga.gainPerBit * readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN3_GND.getMux() | MODE.SINGLE.getMode());
    }

    /**
     * start continuous reading
     *
     * @param channel       ad converter chanel
     * @param threshold     threshold for trigger new value change event (+- voltage)
     * @param readFrequency read frequency to get new value from device, must be lower than 1/2
     *                      sampling rate of device
     */
    public void startFastContiniousReading(int channel, double threshold, int readFrequency) {
        //only if continious reading is not set to true by other component
        if (!continiousReadingActive){
            //get mux from channel
            MUX mux = MUX.AIN0_GND;
            switch (channel){
                case 1 : mux = MUX.AIN1_GND; break;
                case 2 : mux = MUX.AIN2_GND; break;
                case 3 : mux = MUX.AIN3_GND; break;
            }
            fastReadContiniousValue(CONFIG_REGISTER_TEMPLATE | mux.getMux() | MODE.CONTINUOUS.getMode(), threshold, readFrequency);
            continiousReadingActive = true;
        }
    }

    /**
     * stops continious reading
     */
    public void stopFastContiniousReading() {
        logInfo("Stop continious reading");
        // write single shot configuration to stop reading process in device
        writeConfigRegister(CONFIG_REGISTER_TEMPLATE | MUX.AIN0_GND.getMux() | MODE.SINGLE.getMode());
        continiousReadingActive = false;
    }

    /**
     * start continuous reading
     *
     * @param threshold     threshold for trigger new value change event (+- digit)
     * @param readFrequency read frequency to get new value from device, must be lower than 1/2
     *                      sampling rate of device
     */
    public void startSlowContiniousReading(double threshold, int readFrequency) {
        //only start continious Reading if it is not already running because of other component
        if(!continiousReadingActive){
            slowReadContiniousValue(threshold, readFrequency);
            continiousReadingActive = true;
        }
    }

    /**
     * stops continious reading
     */
    public void stopSlowReadContiniousReading() {
        logInfo("Stop continious reading");
        continiousReadingActive = false;
    }

    /**
     * Returns voltage value from fast continious reading
     *
     * @return voltage value
     */
    public double getFastContiniousReadAI() {
        return getSlowContiniousReadAIn0();
    }

    /**
     * Returns voltage value from slow continious reading
     *
     * @return voltage value
     */
    public double getSlowContiniousReadAIn0() {
        if (!continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring not active");
        return pga.gainPerBit() * actualValue[0];
    }

    /**
     * Returns voltage value from slow continious reading
     *
     * @return voltage value
     */
    public double getSlowContiniousReadAIn1() {
        if (!continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring not active");
        return pga.gainPerBit() * actualValue[1];
    }

    /**
     * Returns voltage value from slow continious reading
     *
     * @return voltage value
     */
    public double getSlowContiniousReadAIn2() {
        if (!continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring not active");
        return pga.gainPerBit() * actualValue[2];
    }

    /**
     * Returns voltage value from slow continious reading
     *
     * @return voltage value
     */
    public double getSlowContiniousReadAIn3() {
        if (!continiousReadingActive) throw new ContiniousMeasuringException("Continious measuring not active");
        return pga.gainPerBit() * actualValue[3];
    }

    /**
     * Return pi4j context
     *
     * @return pi4j context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Return bus address
     *
     * @return bus address
     */
    public int getI2CBus() {
        return i2cBus;
    }

    /**
     * Return device name
     *
     * @return device name
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Return GAIN object with bit structure for configuration and resolution (gain per bit)
     *
     * @return GAIN object
     */
    public GAIN getPga() {
        return pga;
    }

    /**
     * Retrun sampling rate from device
     *
     * @return samplingrate
     */
    public int getSamplingRate() {
        return dr.getSpS();
    }

    /**
     * Sets or disables the handler for the onValueChange event.
     * This event gets triggered whenever the analog value
     * from the device changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setRunnableFastRead(Runnable method) {
        this.runnableFastRead = method;
    }

    /**
     * Sets or disables the handler for the onValueChange event from continious slow read.
     * This event gets triggered whenever the analog value
     * from the device changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setRunnableSlowReadChannel0(Runnable method) {
        this.runnableSlowRead[0] = method;
    }

    /**
     * Sets or disables the handler for the onValueChange event from continious slow read.
     * This event gets triggered whenever the analog value
     * from the device changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setRunnableSlowReadChannel1(Runnable method) {
        this.runnableSlowRead[1] = method;
    }

    /**
     * Sets or disables the handler for the onValueChange event from continious slow read.
     * This event gets triggered whenever the analog value
     * from the device changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setRunnableSlowReadChannel2(Runnable method) {
        this.runnableSlowRead[2] = method;
    }

    /**
     * Sets or disables the handler for the onValueChange event from continious slow read.
     * This event gets triggered whenever the analog value
     * from the device changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setRunnableSlowReadChannel3(Runnable method) {
        this.runnableSlowRead[3] = method;
    }

    /**
     * disables all handlers
     */
    public void deregisterAll() {
        setRunnableFastRead(null);
        for (int i = 1; i < 4; i++){
            runnableSlowRead[i] = null;
        }
    }

    /**
     * Setup configuration for config register
     */
    private void createTemplateConfiguration() {
        CONFIG_REGISTER_TEMPLATE = os | pga.gain | dr.getConf() | compMode | compPol | compLat | compQue;
    }

    /**
     * read configuration from device
     *
     * @return configuration from device
     */
    private int readConfigRegister() {
        String[] osInfo = {"0 : Device is currently performing a conversion\n", "1 : Device is not currently performing a conversion\n"};

        String[] muxInfo = {"000 : AINP = AIN0 and AINN = AIN1\n", "001 : AINP = AIN0 and AINN = AIN3\n", "010 : AINP = AIN1 and AINN = AIN3\n", "011 : AINP = AIN2 and AINN = AIN3\n", "100 : AINP = AIN0 and AINN = GND\n", "101 : AINP = AIN1 and AINN = GND\n", "110 : AINP = AIN2 and AINN = GND\n", "111 : AINP = AIN3 and AINN = GND\n"};

        String[] pgaInfo = {"000 : FSR = ±6.144 V(1)\n", "001 : FSR = ±4.096 V(1)\n", "010 : FSR = ±2.048 V\n", "011 : FSR = ±1.024 V\n", "100 : FSR = ±0.512 V\n", "101 : FSR = ±0.256 V\n", "110 : FSR = ±0.256 V\n", "111 : FSR = ±0.256 V\n"};

        String[] modeInfo = {"0 : Continuous-conversion mode\n", "1 : Single-shot mode or power-down state\n"};

        String[] drInfo = {"000 : 8 SPS\n", "001 : 16 SPS\n", "010 : 32 SPS\n", "011 : 64 SPS\n", "100 : 128 SPS\n", "101 : 250 SPS\n", "110 : 475 SPS\n", "111 : 860 SPS\n"};

        String[] compModeInfo = {"0 : Traditional comparator (default)\n", "1 : Window comparator\n"};

        String[] compPolInfo = {"0 : Active low (default)\n", "1 : Active high\n"};

        String[] compLatInfo = {"0 : Nonlatching comparator\n", "1 : Latching comparator\n"};

        String[] compQueInfo = {"00 : Assert after one conversion\n", "01 : Assert after two conversions\n", "10 : Assert after four conversions\n", "11 : Disable comparator and set ALERT/RDY pin to high-impedance\n"};

        //get configuration from device
        int result = i2c.readRegisterWord(CONFIG_REGISTER);

        //create logger message
        StringBuilder debugInfo = new StringBuilder();
        //check os
        debugInfo.append((osInfo[result >> 15]));
        //check mux
        debugInfo.append(muxInfo[(result & MUX.CLR_OTHER_CONF_PARAM.getMux()) >> 12]);
        //check pga
        debugInfo.append(pgaInfo[(result & PGA.CLR_OTHER_CONF_PARAM.getPga()) >> 9]);
        //check mode
        debugInfo.append(modeInfo[(result & MODE.CLR_OTHER_CONF_PARAM.getMode()) >> 8]);
        //check dr
        debugInfo.append(drInfo[(result & DR.CLR_OTHER_CONF_PARAM.getConf()) >> 5]);
        //check comp mode
        debugInfo.append(compModeInfo[(result & COMP_MODE.CLR_OTHER_CONF_PARAM.getCompMode()) >> 4]);
        //check comp pol
        debugInfo.append(compPolInfo[(result & COMP_POL.CLR_OTHER_CONF_PARAM.getCompPol()) >> 3]);
        //check comp lat
        debugInfo.append(compLatInfo[(result & COMP_LAT.CLR_OTHER_CONF_PARAM.getLatching()) >> 2]);
        //check comp que
        debugInfo.append(compQueInfo[result & COMP_QUE.CLR_OTHER_CONF_PARAM.getCompQue()]);

        logger.config(debugInfo.toString());

        return result;
    }

    /**
     * Sends a request to device and wait for response
     *
     * @param config Configuration for config register
     * @return int conversion register
     */
    private int readSingleShot(int config) {
        //write configuration to device
        int confCheck = writeConfigRegister(config);
        //check if configuration is correct written on device, ignore first bit (os)
        if ((confCheck & OS.CLR_CURRENT_CONF_PARAM.getOs()) != (config & OS.CLR_CURRENT_CONF_PARAM.getOs()))
            throw new ConfigException("Configuration not correctly written to device.");
        //read actual ad value from device
        int result = i2c.readRegisterWord(CONVERSION_REGISTER);
        //logInfo("readIn: " + config + ", raw " + result);
        return result;
    }

    /**
     * Sends configuration for continious reading to device, updates actual value from analog input
     * and triggers valueChange event
     *
     * @param config        Configuration for config register
     * @param threshold     threshold for trigger new value change event
     * @param readFrequency read frequency to get new value from device, must be lower than
     *                      the sampling rate of the device
     */
    private void fastReadContiniousValue(int config, double threshold, int readFrequency) {
        if (readFrequency < dr.getSpS()) {
            logInfo("Start continious reading");
            //set configuration
            writeConfigRegister(config);
            //start new thread for continuous reading
            new Thread(() -> {
                while (continiousReadingActive) {
                    int result = readConversionRegister();
                    //logInfo("Current value: " + result);
                    //convert threshold voltage to digits
                    int thresholdDigits = (int) (threshold / pga.gainPerBit);
                    if (oldValue[0]- thresholdDigits > result || oldValue[0] + thresholdDigits < result) {
                        //logInfo("New event triggered on value change, old value: " + oldValue.get() + " , new value: " + result);
                        oldValue[0] = actualValue[0];
                        actualValue[0] = result;
                        runnableFastRead.run();
                    }
                    delay(1 / readFrequency * 1000);
                }
            }).start();
        } else {
            logError("readFrequency to high");
        }
    }

    /**
     * Sends, for each channel, a request to device and wait for response. Enters all responses in actualValue array.
     * Waits for the rest of readFrequency time.
     *
     * @param threshold     threshold for trigger new value change event
     * @param readFrequency read frequency to get new value from device, must be lower than 1/2
     *                      the sampling rate of the device
     */
    private void slowReadContiniousValue(double threshold, int readFrequency){
        //summ of readFrequency of all channels must be lower than 1/2 sampling rate
        if (readFrequency * numberOfChannels * 2 < dr.getSpS()) {
            logInfo("Start continious reading");
            //start new thread for continuous reading
            new Thread(()-> {
                while (continiousReadingActive) {
                    //start measuring time
                    long startTime = System.nanoTime();

                    int[] result = new int[4];
                    //at least on chanel bust be activated
                    result[0] = readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN0_GND.getMux() | MODE.SINGLE.getMode());
                    //if at least two channels are activated
                    if (numberOfChannels > 1){
                        result[1] = readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN1_GND.getMux() | MODE.SINGLE.getMode());
                    }
                    //if at least three channels are activated
                    if (numberOfChannels > 2){
                        result[2] = readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN2_GND.getMux() | MODE.SINGLE.getMode());
                    }
                    //if all 4 channels are activated
                    if (numberOfChannels > 3){
                        result[3] = readSingleShot(CONFIG_REGISTER_TEMPLATE | MUX.AIN3_GND.getMux() | MODE.SINGLE.getMode());
                    }
                    //convert threshold voltage to digits
                    int thresholdDigits = (int) (threshold / pga.gainPerBit);
                    for(int i = 0; i < numberOfChannels; i++){
                        if (oldValue[i]- thresholdDigits > result[i] || oldValue[i] + thresholdDigits < result[i]) {
                            //logInfo("New event triggered on value change, old value: " + oldValue.get() + " , new value: " + result);
                            oldValue[i] = actualValue[i];
                            actualValue[i] = result[i];
                            if (runnableSlowRead[i] != null){
                                runnableSlowRead[i].run();
                            }
                        }
                    }
                    //stop measuring time
                    long stopTime = System.nanoTime();
                    long delta = stopTime - startTime;
                    long restDelay = (1/readFrequency * 1000) -delta;
                    restDelay = (restDelay > 0)? restDelay : 0;
                    //wait for rest of the cycletime
                    delay(restDelay);
                }
            }).start();
        } else {
            logError("readFrequency to high");
        }
    }



    /**
     * Build a I2C Configuration to use the AD convertor
     *
     * @param pi4j   PI4J Context
     * @param bus    I2C Bus address
     * @param device I2C Device address
     * @return I2C configuration
     */
    private static I2CConfig buildI2CConfig(Context pi4j, int bus, int device, String deviceId) {
        return I2C.newConfigBuilder(pi4j).id("I2C-" + device + "@" + bus).name(deviceId).bus(bus).device(device).build();
    }

    /**
     * I2C Address Selection
     * The ADS1115 has one address pin, ADDR, that configures the I2C address of the device. This pin can be
     * connected to GND, VDD, SDA, or SCL, allowing for four different addresses to be selected with one pin.
     * Use the GND, VDD and SCL addresses first. If SDA is used as the device address, hold the SDA line low
     * for at least 100 ns after the SCL line goes low to make sure the device decodes the address correctly
     * during I2C communication.
     * <p>
     * Address that can be used
     * {@link #GND}
     * {@link #VDD}
     * {@link #SDA}
     * {@link #SCL}
     */
    public enum ADDRESS {
        /**
         * Device address if pin is connected to GND
         */
        GND(0x48),
        /**
         * Device address if pin is connected to VDD
         */
        VDD(0x49),
        /**
         * Device address if pin is connected to SDA
         */
        SDA(0x4A),
        /**
         * Device address if pin is connected to SCL
         */
        SCL(0x4B);
        /**
         * device address on I2C
         */
        private final int address;

        /**
         * Set the address for a device on an I2C bus
         *
         * @param address device address on I2C
         */
        ADDRESS(int address) {
            this.address = address;
        }

        /**
         * Retunrs the address from the device on an I2C bus
         *
         * @return Returns the address form the device
         */
        public int getAddress() {
            return address;
        }
    }

    /**
     * A programmable gain amplifier (PGA) is implemented before the ΔΣ ADC of the ADS1115. The
     * full-scale range is configured by bits PGA[2:0] in the Config register and can be set to ±6.144 V, ±4.096 V,
     * ±2.048 V, ±1.024 V, ±0.512 V, ±0.256 V.
     * <p>
     * programmable gain amplifier that can be used
     * {@link #GAIN_6_144V}
     * {@link #GAIN_4_096V}
     * {@link #GAIN_2_048V}
     * {@link #GAIN_1_024V}
     * {@link #GAIN_0_512V}
     * {@link #GAIN_0_256V}
     */
    public enum GAIN {
        /**
         * 000 : Full-Scale Range (FSR)  = ±6.144 V
         */
        GAIN_6_144V(PGA.FSR_6_144.pga, 187.5 / 1_000_000),
        /**
         * 001 : FSR = ±4.096 V
         */
        GAIN_4_096V(PGA.FSR_4_096.pga, 125.0 / 1_000_000),
        /**
         * 010 : FSR = ±2.048 V
         */
        GAIN_2_048V(PGA.FSR_2_048.pga, 62.5 / 1_000_000),
        /**
         * 011 : FSR = ±1.024 V
         */
        GAIN_1_024V(PGA.FSR_1_024.pga, 31.25 / 1_000_000),
        /**
         * 100 : FSR = ±0.512 V
         */
        GAIN_0_512V(PGA.FSR_0_512.pga, 15.625 / 1_000_000),
        /**
         * 101 : FSR = ±0.256 V
         */
        GAIN_0_256V(PGA.FSR_0_256.pga, 7.8125 / 1_000_000);
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
         * @param gain       configuration for gain
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
     * Comparator queue and disable
     * These bits perform two functions. When set to 11, the comparator is disabled and
     * the ALERT/RDY pin is set to a high-impedance state. When set to any other
     * value, the ALERT/RDY pin and the comparator function are enabled, and the set
     * value determines the number of successive conversions exceeding the upper or
     * lower threshold required before asserting the ALERT/RDY pin.
     * <p>
     * teh following comparator queue can be used
     * {@link #ASSERT_ONE}
     * {@link #ASSERT_TWO}
     * {@link #ASSERT_FOUR}
     * {@link #DISABLE_COMP}
     */
    public enum COMP_QUE {
        /**
         * Assert after one conversion
         */
        ASSERT_ONE(0b0000_0000_0000_0000),
        /**
         * Assert after two conversions
         */
        ASSERT_TWO(0b0000_0000_0000_0001),
        /**
         * Assert after four conversions
         */
        ASSERT_FOUR(0b0000_0000_0000_0010),
        /**
         * Disable comparator and set ALERT/RDY pin to high-impedance
         */
        DISABLE_COMP(0b0000_0000_0000_0011),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0000_0000_0000_0011),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b1111_1111_1111_1100);
        /**
         * comparator queue
         */
        private final int compQue;

        /**
         * Set comparator queue for configuration
         *
         * @param compQue comparator queue configuration
         */
        COMP_QUE(int compQue) {
            this.compQue = compQue;
        }

        /**
         * Retruns comparator queue configuration
         *
         * @return comparator queue configuration
         */
        public int getCompQue() {
            return this.compQue;
        }

    }

    /**
     * Latching comparator
     * This bit controls whether the ALERT/RDY pin latches after being asserted or
     * clears after conversions are within the margin of the upper and lower threshold
     * values.
     * <p>
     * the following mode can be used
     * {@link #NON_LATCH}
     * {@link #DO_LATCH}
     */
    public enum COMP_LAT {
        /**
         * Nonlatching comparator. The ALERT/RDY pin does not latch when asserted.
         */
        NON_LATCH(0b0000_0000_0000_0000),
        /**
         * Latching comparator. The asserted ALERT/RDY pin remains latched until
         * conversion data are read by the master or an appropriate SMBus alert response
         * is sent by the master. The device responds with its address, and it is the lowest
         * address currently asserting the ALERT/RDY bus line.
         */
        DO_LATCH(0b0000_0000_0000_0100),
        /**
         * latching
         */
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0000_0000_0000_0100),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b1111_1111_1111_1011);
        private final int latching;

        /**
         * Set latching for configuration
         *
         * @param compLat comparator latching configuration
         */
        COMP_LAT(int compLat) {
            this.latching = compLat;
        }

        /**
         * Retruns configuration for comparator latching
         *
         * @return comparator latching configuration
         */
        public int getLatching() {
            return this.latching;
        }
    }

    /**
     * Comparator polarity
     * This bit controls the polarity of the ALERT/RDY pin.
     * <p>
     * The following polarities can be set
     * {@link #ACTIVE_LOW}
     * {@link #ACTIVE_HIGH}
     */
    public enum COMP_POL {
        /**
         * Active low
         */
        ACTIVE_LOW(0b0000_0000_0000_0000),
        /**
         * Active high
         */
        ACTIVE_HIGH(0b0000_0000_0000_1000),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0000_0000_0000_1000),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b1111_1111_1111_0111);
        /**
         * comparator polarity
         */
        private final int compPol;

        /**
         * Set comparator polarisation for configuration
         *
         * @param compPol comparator polarisation for configuration
         */
        COMP_POL(int compPol) {
            this.compPol = compPol;
        }

        /**
         * Returns configuration of comparator polarisation
         *
         * @return comparator polarisation
         */
        public int getCompPol() {
            return this.compPol;
        }
    }

    /**
     * Comparator mode
     * This bit configures the comparator operating mode.
     * <p>
     * The following modes can be set
     * {@link #TRAD_COMP}
     * {@link #WINDOW_COMP}
     */
    public enum COMP_MODE {
        /**
         * Traditional comparator (
         */
        TRAD_COMP(0b0000_0000_0000_0000),
        /**
         * Window comparator
         */
        WINDOW_COMP(0b0000_0000_0001_0000),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0000_0000_0001_0000),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b1111_1111_1110_1111);
        /**
         * comparator mode
         */
        private final int compMode;

        /**
         * Set mode for comparator configuration
         *
         * @param compMOde comparator mode for configuration
         */
        COMP_MODE(int compMOde) {
            this.compMode = compMOde;
        }

        /**
         * Returns configuration of comparator mode
         *
         * @return configuration of comparator mode
         */
        public int getCompMode() {
            return this.compMode;
        }

    }

    /**
     * Data rate
     * These bits control the data rate setting
     * <p>
     * The following modes can be set
     * {@link #SPS_8}
     * {@link #SPS_16}
     * {@link #SPS_32}
     * {@link #SPS_64}
     * {@link #SPS_128}
     * {@link #SPS_250}
     * {@link #SPS_475}
     * {@link #SPS_860}
     */
    public enum DR {
        /**
         * 8 sampling per second
         */
        SPS_8(0b0000_0000_0000_0000, 8),
        /**
         * 16 sampling per second
         */
        SPS_16(0b0000_0000_0010_0000, 16),
        /**
         * 32 sampling per second
         */
        SPS_32(0b0000_0000_0100_0000, 32),
        /**
         * 64 sampling per second
         */
        SPS_64(0b0000_0000_0110_0000, 64),
        /**
         * 128 sampling per second
         */
        SPS_128(0b0000_0000_1000_0000, 128),
        /**
         * 250 sampling per second
         */
        SPS_250(0b0000_0000_1010_0000, 250),
        /**
         * 8475sampling per second
         */
        SPS_475(0b0000_0000_1100_0000, 475),
        /**
         * 860 sampling per second
         */

        SPS_860(0b0000_0000_1110_0000, 860),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0000_0000_1110_0000, 0),

        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */

        CLR_CURRENT_CONF_PARAM(0b1111_1111_0001_1111, 0);
        /**
         * configuration
         */
        private final int conf;

        /**
         * sampling per second
         */
        private final int sps;

        /**
         * Set sampling rate for configuration
         *
         * @param sps sampling rate for configuration
         */
        DR(int conf, int sps) {
            this.conf = conf;
            this.sps = sps;
        }

        /**
         * Retruns sampling rate
         *
         * @return sampling rate
         */
        public int getSpS() {
            return this.sps;
        }

        /**
         * Returns samplingrate for configuration
         *
         * @return sampling rate for configuration
         */
        public int getConf() {
            return this.conf;
        }
    }

    /**
     * Device operating mode
     * This bit controls the operating mode.
     * <p>
     * The following modes can be set
     * {@link #CONTINUOUS}
     * {@link #SINGLE}
     */
    public enum MODE {
        /**
         * Continuous-conversion mode
         */
        CONTINUOUS(0b0000_0000_0000_0000),
        /**
         * Single-shot mode or power-down state
         */
        SINGLE(0b0000_0001_0000_0000),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0000_0001_0000_0000),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b1111_1110_1111_1111);
        /**
         * device operation mode
         */
        private final int mode;

        /**
         * Set device operation mode for configuration
         *
         * @param mode operation mode for configuration
         */
        MODE(int mode) {
            this.mode = mode;
        }

        /**
         * Returns configured operation mode
         *
         * @return configured operation mode
         */
        public int getMode() {
            return this.mode;
        }
    }

    /**
     * Programmable gain amplifier configuration
     * These bits set the FSR of the programmable gain amplifier.
     * <p>
     * The following gain amplifier can be configured
     * {@link #FSR_6_144}
     * {@link #FSR_4_096}
     * {@link #FSR_2_048}
     * {@link #FSR_1_024}
     * {@link #FSR_0_512}
     * {@link #FSR_0_256}
     */
    public enum PGA {
        /**
         * 000 : FSR = ±6.144 V
         */
        FSR_6_144(0b0000_0000_0000_0000),
        /**
         * 001 : FSR = ±4.096 V
         */
        FSR_4_096(0b0000_0010_0000_0000),
        /**
         * 010 : FSR = ±2.048 V
         */
        FSR_2_048(0b0000_0100_0000_0000),
        /**
         * 011 : FSR = ±1.024 V
         */
        FSR_1_024(0b0000_0110_0000_0000),
        /**
         * 100 : FSR = ±0.512 V
         */
        FSR_0_512(0b0000_1000_0000_0000),
        /**
         * 101 : FSR = ±0.256 V
         */
        FSR_0_256(0b0000_1010_0000_0000),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0000_1110_0000_0000),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b1111_0001_1111_1111);
        /**
         * Programmable gain amplifier
         */
        private final int pga;

        /**
         * Set configuration for programmable gain amplifier
         *
         * @param pag Programmable gain amplifier configuration
         */
        PGA(int pag) {
            this.pga = pag;
        }

        /**
         * Returns configuration from Programmable gain amplifier
         *
         * @return Programmable gain amplifier configuration
         */
        public int getPga() {
            return this.pga;
        }

    }

    /**
     * Input multiplexer configuration
     * These bits configure the input multiplexer.
     * <p>
     * The following gain amplifier can be configured
     * {@link #AIN0_AIN1}
     * {@link #AIN0_AIN3}
     * {@link #AIN1_AIN3}
     * {@link #AIN2_AIN3}
     * {@link #AIN0_GND}
     * {@link #AIN1_GND}
     * {@link #AIN2_GND}
     * {@link #AIN3_GND}
     */
    public enum MUX {
        /**
         * 000 : AINP = AIN0 and AINN = AIN1
         */
        AIN0_AIN1(0b0000_0000_0000_0000),
        /**
         * 001 : AINP = AIN0 and AINN = AIN3
         */
        AIN0_AIN3(0b0001_0000_0000_0000),
        /**
         * 010 : AINP = AIN1 and AINN = AIN3
         */
        AIN1_AIN3(0b0010_0000_0000_0000),
        /**
         * 011 : AINP = AIN2 and AINN = AIN3
         */
        AIN2_AIN3(0b0011_0000_0000_0000),
        /**
         * 100 : AINP = AIN0 and AINN = GND
         */
        AIN0_GND(0b0100_0000_0000_0000),
        /**
         * 101 : AINP = AIN1 and AINN = GND
         */
        AIN1_GND(0b0101_0000_0000_0000),
        /**
         * 110 : AINP = AIN2 and AINN = GND
         */
        AIN2_GND(0b0110_0000_0000_0000),
        /**
         * 111 : AINP = AIN3 and AINN = GND
         */
        AIN3_GND(0b0111_0000_0000_0000),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b0111_0000_0000_0000),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b1000_1111_1111_1111);
        /**
         * Input multiplexer configuration
         */
        private final int mux;

        /**
         * Set configuration for Input multiplexer
         *
         * @param mux Input multiplexer configuration
         */
        MUX(int mux) {
            this.mux = mux;
        }

        /**
         * Retruns configuration for Input multiplexer
         *
         * @return Input multiplexer configuration
         */
        public int getMux() {
            return this.mux;
        }
    }

    /**
     * Operational status or single-shot conversion start
     * This bit determines the operational status of the device. OS can only be written
     * when in power-down state and has no effect when a conversion is ongoing.
     * <p>
     * The following operation can be set
     * {@link #WRITE_START}
     * {@link #READ_CONV}
     * {@link #READ_NO_CONV}
     */
    public enum OS {
        /**
         * When writing: start a single conversion (when in power-down state)
         */
        WRITE_START(0b1000_0000_0000_0000),
        /**
         * When reading: Device is currently performing a conversion
         */
        READ_CONV(0b0000_0000_0000_0000),
        /**
         * When reading: Device is not currently performing a conversion
         */
        READ_NO_CONV(0b1000_0000_0000_0000),
        /**
         * With an AND operation all other parameters will be set to 0
         */
        CLR_OTHER_CONF_PARAM(0b1000_0000_0000_0000),
        /**
         * With an AND operation the current parameters will be set to 0
         * all other parameters remain unchanged
         */
        CLR_CURRENT_CONF_PARAM(0b0111_1111_1111_1111);
        /**
         * Operational status or single-shot conversion start
         */
        private final int os;

        /**
         * Set parameter for os
         *
         * @param os parameter
         */
        OS(int os) {
            this.os = os;
        }

        /**
         * Returns parameter for OS configuration
         *
         * @return os configuration parameter
         */
        public int getOs() {
            return this.os;
        }
    }
}