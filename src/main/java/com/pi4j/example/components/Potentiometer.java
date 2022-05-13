package com.pi4j.example.components;

public class Potentiometer extends Component {
    /**
     * ads1115 instance
     */
    private final ADS1115 ads1115;

    /**
     * Input multiplexer configuration
     */
    private final ADS1115.MUX mux;

    /**
     * min value which potentiometer has reached
     */
    private double minValue;

    /**
     * max value which potentiometer has reached
     */
    private double maxValue;

    /**
     * Runnable code when potentiometer change value
     */
    private Runnable runnable;

    /**
     * continious reading is active
     */
    private boolean continiousReadingActive;

    /**
     * value from last read
     */
    private double oldValue;
    /**
     * voltage value from current read
     */
    private double actualValue;

    /**
     * fast continuous reading is active
     */
    private boolean fastContiniousReadingActive = false;

    /**
     * slow continious reading is active
     */
    private boolean slowContiniousReadingActive = false;

    /**
     * Create a new potentiometer component with custom mux and custom maxVoltage
     *
     * @param ads1115    ads instance
     * @param mux        custom mux
     * @param maxVoltage custom maxVoltage
     */
    public Potentiometer(ADS1115 ads1115, ADS1115.MUX mux, double maxVoltage) {
        this.ads1115 = ads1115;
        this.minValue = ads1115.getPga().gain() * 0.1;
        this.maxValue = maxVoltage;
        this.mux = mux;
    }

    /**
     * Create a new potentiometer componentn with default mux and maxVoltage for Raspberry pi
     *
     * @param ads1115 ads instance
     */
    public Potentiometer(ADS1115 ads1115) {
        this.ads1115 = ads1115;
        this.minValue = ads1115.getPga().gain() * 0.1;
        this.maxValue = 3.3;
        this.mux = ADS1115.MUX.AIN0_GND;
    }

    /**
     * Returns actual voltage from potentiometer
     *
     * @return voltage from potentiometer
     */
    public double singleShotGetVoltage() {
        double result = 0.0;
        if (mux == ADS1115.MUX.AIN0_GND) {
            result = ads1115.singleShotAIn0();
        } else if (mux == ADS1115.MUX.AIN1_GND) {
            result = ads1115.singleShotAIn1();
        } else if (mux == ADS1115.MUX.AIN2_GND) {
            result = ads1115.singleShotAIn2();
        } else if (mux == ADS1115.MUX.AIN3_GND) {
            result = ads1115.singleShotAIn3();
        }

        if (result < minValue) {
            minValue = result;
        } else if (result > maxValue) {
            maxValue = result;
        }
        return result;
    }

    /**
     * Returns normalized value from 0 to 100
     *
     * @return normalized value
     */
    public double singleShotGetNormalizedValue() {
        return singleShotGetVoltage() / maxValue;
    }

    /**
     * Returns actual voltage value from continious reading
     *
     * @return actual voltage value
     */
    public double continiousReadingGetVoltage() {
        return actualValue;
    }

    /**
     * Returns actual normalized value form 0 to 100 from continious reading
     *
     * @return normalized value
     */
    public double continiousReadingGetNormalizedValue() {
        return actualValue / maxValue;
    }

    /**
     * Sets or disables the handler for the onValueChange event.
     * This event gets triggered whenever the value changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setRunnable(Runnable method) {
        this.runnable = method;
    }

    /**
     * Sends configuration for continious reading to device, updates actual value from analog input
     * and triggers valueChange event
     *
     * @param threshold     threshold for trigger new value change event
     * @param readFrequency read frequency to get new value from device, must be lower than 1/2
     *                      sampling rate of device
     */
    private void readContiniousValue(double threshold, int readFrequency) {
        if (readFrequency < ads1115.getSamplingRate()) {
            logInfo("Start continious reading");

            //start new thread for continuous reading
            new Thread(() -> {
                while (continiousReadingActive) {
                    double result = singleShotGetVoltage();
                    //logInfo("Current value: " + result);
                    if (oldValue - threshold > result || oldValue + threshold < result) {
                        //logInfo("New event triggered on value change, old value: " + oldValue + " , new value: " + result);
                        oldValue = actualValue;
                        actualValue = result;
                        runnable.run();
                    }
                    delay((long) Math.ceil(1000.0 / readFrequency));
                }
            }).start();
        } else {
            logError("readFrequency to high");
        }
    }

    /**
     * start slow continuous reading. In this mode, up to 4 devices can be connected to the analog to digital
     * converter. For each device a single read command is sent to the ad converter and waits for the response.
     * The maximum sampling frequency of the analog signals depends on how many devices are connected to the AD
     * converter at the same time.
     * The maximum allowed sampling frequency of the signal is 1/2 the sampling rate of the ad converter.
     * The reciprocal of this sampling rate finally results in the minimum response time to a signal request.
     * (the delay of the bus is not included).
     * <p>
     * This leads to the following table for the maximum allowed readFrequency by a sampling rate of 128 sps:
     * 1 chanel in use -> readFrequency max 64Hz (min. response time = 16ms)
     * 2 chanel in use -> readFrequency max 32Hz (min. response time = 32ms)
     * 3 chanel in use -> readFrequency max 21Hz (min. response time = 48ms)
     * 4 chanel in use -> readFrequency max 16Hz (min. response time = 63ms)
     *
     * @param threshold     threshold for trigger new value change event (+- digit)
     * @param readFrequency read frequency to get new value from device, must be lower than 1/2
     *                      sampling rate of device
     */
    public void startSlowContiniousReading(double threshold, int readFrequency) {
        if (fastContiniousReadingActive) {
            logDebug("fast continious reading currently active");
        } else {
            //set slow continuous reading active to lock fast continious reading
            slowContiniousReadingActive = true;
            continiousReadingActive = true;
            readContiniousValue(threshold, readFrequency);
        }
    }


    /**
     * stops slow continious reading
     */
    public void stopSlowContiniousReading() {
        logInfo("Stop continious reading");
        slowContiniousReadingActive = false;
        continiousReadingActive = false;
    }

    /**
     * Starts fast continious reading. In this mode only on device can be connected to the ad converter.
     * The maximum allowed readFrequency ist equal to the sample rate of the ad converter
     *
     * @param threshold     threshold for trigger new value change event (+- digit)
     * @param readFrequency read frequency to get new value from device, must be lower than the
     *                      sampling rate of the device
     */
    public void startFastContiniousReading(int threshold, int readFrequency) {
        if (slowContiniousReadingActive) {
            logDebug("slow continious reading currently active");
        } else {
            //set fast continuous reading active to lock slow continious reading
            fastContiniousReadingActive = true;
            //set continious reading active to lock single shot reading
            continiousReadingActive = true;
            //set runnable code on ads1115 to update actualValue if value change is triggered on ads1115
            ads1115.setRunnable(() -> {
                double result = ads1115.continiousReadAI();
                logInfo("New event triggered on value change, old value: " + oldValue + " , new value: " + result);
                oldValue = actualValue;
                actualValue = result;
                runnable.run();
            });
            //start continious reading on ads1115
            ads1115.startContiniousReading(mux, threshold, readFrequency);
        }
    }

    /**
     * stops fast continious reading
     */
    public void stopFastContiniousReading() {
        logInfo("Stop fast continious reading");
        fastContiniousReadingActive = false;
        continiousReadingActive = false;
        //set runnable to null
        ads1115.setRunnable(null);
        ads1115.stopContiniousReading();
    }

    public void deregisterAll(){
        ads1115.setRunnable(null);
        setRunnable(null);
    }
}
