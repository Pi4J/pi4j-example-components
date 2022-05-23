package com.pi4j.example.components;

import com.pi4j.config.exception.ConfigException;

import java.util.function.Consumer;

public class Potentiometer extends Component {
    /**
     * ads1115 instance
     */
    private final ADS1115 ads1115;

    /**
     * AD channel connected to potentiometer (must be between 0 and 3)
     */
    private final int channel;
    /**
     * min value which potentiometer has reached
     */
    private double minValue;

    /**
     * max value which potentiometer has reached
     */
    private double maxValue;

    /**
     * fast continuous reading is active
     */
    private boolean fastContiniousReadingActive = false;

    /**
     * slow continious reading is active
     */
    private boolean slowContiniousReadingActive = false;

    /**
     * Create a new potentiometer component with custom chanel and custom maxVoltage
     *
     * @param ads1115    ads instance
     * @param chanel     custom ad chanel
     * @param maxVoltage custom maxVoltage
     */
    public Potentiometer(ADS1115 ads1115, int chanel, double maxVoltage) {
        this.ads1115 = ads1115;
        this.minValue = ads1115.getPga().gain() * 0.1;
        this.maxValue = maxVoltage;
        this.channel = chanel;

        //check if chanel is in range of ad converter
        if (chanel < 0 || chanel > 3) {
            throw new ConfigException("Channel number for ad converter not possible, choose channel between 0 to 3");
        }
    }

    /**
     * Create a new potentiometer component with default chanel and maxVoltage for Raspberry pi
     *
     * @param ads1115 ads instance
     */
    public Potentiometer(ADS1115 ads1115) {
        this.ads1115 = ads1115;
        this.minValue = ads1115.getPga().gain() * 0.1;
        this.maxValue = 3.3;
        this.channel = 0;
    }

    /**
     * Returns actual voltage from potentiometer
     *
     * @return voltage from potentiometer
     */
    public double singleShotGetVoltage() {
        double result = 0.0;
        switch (channel) {
            case 0:
                result = ads1115.singleShotAIn0();
                break;
            case 1:
                result = ads1115.singleShotAIn1();
                break;
            case 2:
                result = ads1115.singleShotAIn2();
                break;
            case 3:
                result = ads1115.singleShotAIn3();
                break;
        }
        updateMinMaxValue(result);
        return result;
    }

    /**
     * Returns normalized value from 0 to 1
     *
     * @return normalized value
     */
    public double singleShotGetNormalizedValue() {
        return singleShotGetVoltage() / maxValue;
    }

    /**
     * Sets or disables the handler for the onValueChange event.
     * This event gets triggered whenever the value changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setConsumerFastRead(Consumer<Double> method) {
        ads1115.setConsumerFastRead((value)->{
            updateMinMaxValue(value);
            value = value / maxValue;
            method.accept(value);
        });
    }

    /**
     * Sets or disables the handler for the onValueChange event.
     * This event gets triggered whenever the value changes.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void setConsumerSlowReadChan(Consumer<Double> method) {
        switch (channel) {
            case 0:
                ads1115.setConsumerSlowReadChannel0((value)->{
                    updateMinMaxValue(value);
                    value = value / maxValue;
                    method.accept(value);
                });
                break;
            case 1:
                ads1115.setConsumerSlowReadChannel1((value)->{
                    updateMinMaxValue(value);
                    value = value / maxValue;
                    method.accept(value);
                });
                break;
            case 2:
                ads1115.setConsumerSlowReadChannel2((value)->{
                    updateMinMaxValue(value);
                    value = value / maxValue;
                    method.accept(value);
                });
                break;
            case 3:
                ads1115.setConsumerSlowReadChannel3((value)->{
                    updateMinMaxValue(value);
                    value = value / maxValue;
                    method.accept(value);
                });
                break;
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
     * @param threshold     threshold for trigger new value change event (+- voltage)
     * @param readFrequency read frequency to get new value from device, must be lower than 1/2
     *                      sampling rate of device
     */
    public void startSlowContiniousReading(double threshold, int readFrequency) {
        if (fastContiniousReadingActive) {
            logDebug("fast continious reading currently active");
        } else {
            //set slow continuous reading active to lock fast continious reading
            slowContiniousReadingActive = true;
            ads1115.startSlowContiniousReading(channel, threshold, readFrequency);
        }
    }

    /**
     * stops slow continious reading
     */
    public void stopSlowContiniousReading() {
        logInfo("Stop continious reading");
        slowContiniousReadingActive = false;
        ads1115.stopSlowReadContiniousReading(channel);
    }

    /**
     * Starts fast continious reading. In this mode only on device can be connected to the ad converter.
     * The maximum allowed readFrequency ist equal to the sample rate of the ad converter
     *
     * @param threshold     threshold for trigger new value change event (+- voltage)
     * @param readFrequency read frequency to get new value from device, must be lower than the
     *                      sampling rate of the device
     */
    public void startFastContiniousReading(double threshold, int readFrequency) {
        if (slowContiniousReadingActive) {
            logDebug("slow continious reading currently active");
        } else {
            //set fast continuous reading active to lock slow continious reading
            fastContiniousReadingActive = true;

            //start continious reading on ads1115
            ads1115.startFastContiniousReading(channel, threshold, readFrequency);
        }
    }

    /**
     * stops fast continious reading
     */
    public void stopFastContiniousReading() {
        logInfo("Stop fast continious reading");
        fastContiniousReadingActive = false;
        //stop continious reading
        ads1115.stopFastContiniousReading();
    }

    /**
     * disables all handlers
     */
    public void deregisterAll() {
        switch (channel) {
            case 0:
                ads1115.setConsumerSlowReadChannel0(null);
                break;
            case 1:
                ads1115.setConsumerSlowReadChannel1(null);
                break;
            case 2:
                ads1115.setConsumerSlowReadChannel2(null);
                break;
            case 3:
                ads1115.setConsumerSlowReadChannel3(null);
                break;
        }
    }

    /**
     * Check if new value is bigger than current max value or lower than min value
     * In this case update min or max value
     *
     * @param result value to check against min Max value
     */
    private void updateMinMaxValue(double result) {
        if (result < minValue) {
            minValue = result;
        } else if (result > maxValue) {
            maxValue = result;
        }
    }
}
