package com.pi4j.catalog.components;

import java.time.Duration;
import java.util.function.Consumer;

import com.pi4j.catalog.components.base.Component;
import com.pi4j.catalog.components.base.PIN;

/**
 * An analog joystick needs to use an analog-digital convertor (ADC) to be attached to RaspPi.
 * <p>
 * In this implementation we use an 'Ads115' and attach two 'Potentiometer', using 2 of the ADC channels,
 * and one 'SimpleButton', connected to one of the digital pins, to it.
 * <p>
 * We use the terms 'normalized value' and 'raw value'.
 * <ul>
 *     <li>Normalized values are between -1 and 1. 0 means that joystick is in home position</li>
 *     <li>Raw value is the measured voltage</li>
 * </ul>
 */
public class JoystickAnalog extends Component {
    /**
     * normalized center position
     */
    private final double NORMALIZED_CENTER_POSITION = 0.5;


    private final Ads1115 ads1115;
    /**
     * potentiometer x axis
     */
    private final Potentiometer x;
    /**
     * potentiometer y axis
     */
    private final Potentiometer y;
    /**
     * button push
     *
     * Can be 'null' if joystick doesn't have button functionality or if you don't want to use it
     */
    private final SimpleButton push;

    /**
     * deviation (in Volt) of x-axis if joystick is in home position
     * <P>
     * Ideally the joystick's value should be half of the maximum voltage when in home position.
     * But there are always deviations.
     */
    private double deviationX = 0.0;
    /**
     * deviation (in Volt) of y-axis if joystick is in home position
     * <P>
     * Ideally the joystick's value should be half of the maximum voltage when in home position.
     * But there are always deviations.
     */
    private double deviationY = 0.0;

    /**
     * minimal normalized value on x axis
     */
    private double xMinNormValue;
    /**
     * maximal normalized value on x axis
     */
    private double xMaxNormValue;
    /**
     * minimal normalized value on y axis
     */
    private double yMinNormValue;
    /**
     * maximal normalized value on y axis
     */
    private double yMaxNormValue;


    /**
     * Builds a new JoystickAnalog component with custom input for x-, y-axis, custom pin for push button.
     * ads component needs to be created outside this clas, other channels may be used for other components.
     *
     * @param ads1115      ads object
     * @param channelXAxis analog potentiometer x-axis
     * @param channelYAxis analog potentiometer y-axis
     * @param pin          additional push button on joystick
     */
    public JoystickAnalog(Ads1115 ads1115, Ads1115.Channel channelXAxis, Ads1115.Channel channelYAxis, PIN pin) {
        this(ads1115,
             new Potentiometer(ads1115, channelXAxis),
             new Potentiometer(ads1115, channelYAxis),
             pin != null ? new SimpleButton(ads1115.getPi4j(), pin, true) : null);
    }

    /**
     * @param potentiometerX potentiometer object for x-axis
     * @param potentiometerY potentiometer object for y-axis
     * @param push           simpleButton object for push button on joystick
     */
    JoystickAnalog(Ads1115 ads1115, Potentiometer potentiometerX, Potentiometer potentiometerY, SimpleButton push) {
        this.ads1115 = ads1115;
        this.x       = potentiometerX;
        this.y       = potentiometerY;
        this.push    = push;

        xMinNormValue = 0.1;
        xMaxNormValue = 0.9;
        yMinNormValue = 0.1;
        yMaxNormValue = 0.9;

        calibrateJoystick();
    }

    /**
     * Sets or disables the handler for a value change event.
     * This event gets triggered whenever the x-axis of the joystick is moving.
     * Only a single event handler can be registered at once.
     *
     * @param onChange Event handler to call or null to disable
     */
    public void onHorizontalChange(Consumer<Double> onChange) {
        x.onNormalizedValueChange((raw) -> {

            double value = raw + deviationX;
            //check if min max value are ok
            if (value < xMinNormValue) xMinNormValue = value;
            if (value > xMaxNormValue) xMaxNormValue = value;
            //scale axis from 0 to 1
            if (value < NORMALIZED_CENTER_POSITION) {
                value = (value - xMinNormValue) / (NORMALIZED_CENTER_POSITION - xMinNormValue) / 2;
            } else if (value > NORMALIZED_CENTER_POSITION) {
                value = 1 + (xMaxNormValue - value) / (NORMALIZED_CENTER_POSITION - xMaxNormValue) / 2;
            }

            onChange.accept(rescaleValue(value));
        });
    }

    /**
     * Sets or disables the handler for a value change event.
     * This event gets triggered whenever the y-axis of the joystick is moving.
     * Only a single event handler can be registered at once.
     *
     * @param onChange Event handler to call or null to disable
     */
    public void onVerticalChange(Consumer<Double> onChange) {
        y.onNormalizedValueChange((value) -> {
            value = value + deviationY;

            //check if min max value are ok
            if (value < yMinNormValue) yMinNormValue = value;
            if (value > yMaxNormValue) yMaxNormValue = value;
            //scale axis from 0 to 1
            if (value < NORMALIZED_CENTER_POSITION) {
                value = (value - yMinNormValue) / (NORMALIZED_CENTER_POSITION - yMinNormValue) / 2;
            } else if (value > NORMALIZED_CENTER_POSITION) {
                value = 1 + (yMaxNormValue - value) / (NORMALIZED_CENTER_POSITION - yMaxNormValue) / 2;
            }

            onChange.accept(rescaleValue(value));
        });
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void onDown(Runnable task) {
        if(push == null){
            throw new IllegalStateException("no button set, you can't register an event");
        }
        push.onDown(task);
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void onUp(Runnable task) {
        if(push == null){
            throw new IllegalStateException("no button set, you can't register an event");
        }
        push.onUp(task);
    }

    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void whilePressed(Runnable task, Duration whilePressedDelay) {
        if(push == null){
            throw new IllegalStateException("no button set, you can't register an event");
        }
        push.whilePressed(task, whilePressedDelay);
    }

    /**
     * Start reading of joystick value. Needs to be triggered before any value can be read.
     *
     * @param threshold     delta between old and new value to trigger new event (+- voltage)
     * @param readFrequency update frequency to read new value from ad converter
     */
    public void start(double threshold, int readFrequency) {
        ads1115.startContinuousReading(threshold, readFrequency);
    }

    /**
     * Stop reading of joystick value. If triggered no new value from joystick can be read.
     */
    public void stop() {
        ads1115.stopContinuousReading();
    }

    /**
     * disables all the handlers on joystick events
     */
    @Override
    public void reset() {
        ads1115.reset();
        x.reset();
        y.reset();
        push.reset();
    }

    /**
     * calibrates the center position of the joystick
     */
    private void calibrateJoystick() {
        deviationX = NORMALIZED_CENTER_POSITION - x.readNormalizedValue();
        deviationY = NORMALIZED_CENTER_POSITION - y.readNormalizedValue();
    }


    /**
     * Changes the output value from 0 to 1 to -1 to 1
     *
     * @param in original output value between 0 and 1
     * @return new output value between -1 and 1
     */
    private double rescaleValue(double in) {
        return (in - NORMALIZED_CENTER_POSITION) * 2;
    }
}
