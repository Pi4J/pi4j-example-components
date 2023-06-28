package com.pi4j.catalog.components;

import java.util.function.Consumer;

import com.pi4j.catalog.components.base.Component;

public class Potentiometer extends Component {
    /**
     * ads1115 instance
     */
    private final Ads1115 ads1115;

    /**
     * min value which potentiometer has reached
     */
    private double minValue;

    /**
     * max value which potentiometer has reached
     */
    private double maxValue;


    private final Ads1115.Channel channel;

    /**
     * Create a new potentiometer component with default channel and maxVoltage for Raspberry pi
     *
     * @param ads1115 ads instance
     */
    public Potentiometer(Ads1115 ads1115, Ads1115.Channel channel) {
        this.ads1115 = ads1115;
        this.minValue = 0.2;
        this.maxValue = 3.0;
        this.channel = channel;

        logDebug("Component potentiometer initialized");
    }

    /**
     * Returns actual voltage from potentiometer
     *
     * @return voltage from potentiometer
     */
    public double readCurrentVoltage() {
        double result = ads1115.readValue(channel);
        updateMinMaxValue(result);

        return result;
    }

    /**
     * Returns normalized value from 0 to 1
     *
     * @return normalized value
     */
    public double readNormalizedValue() {
        return normalizeVoltage(readCurrentVoltage());
    }

    /**
     * Sets or disables the handler for the onValueChange event.
     * This event gets triggered whenever the value changes.
     * Only a single event handler can be registered at once.
     *
     * @param onChange Event handler to call or null to disable
     */
    public void onNormalizedValueChange(Consumer<Double> onChange) {
        ads1115.onValueChange(channel, (voltage) -> {
            updateMinMaxValue(voltage);
            onChange.accept(normalizeVoltage(voltage));
        });
    }


    /**
     * disables all handlers
     */
    @Override
    public void reset() {
        ads1115.reset();
    }

    /**
     * Check if new value is bigger than current max value or lower than min value
     * In this case update min or max value
     *
     * @param voltage value to check against min Max value
     */
    private void updateMinMaxValue(double voltage) {
        if (voltage < minValue) {
            minValue = voltage;
        } else if (voltage > maxValue) {
            maxValue = voltage;
        }
    }

    private double normalizeVoltage(double voltage) {
        return voltage / (maxValue - minValue);
    }
}
