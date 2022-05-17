package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.helpers.PIN;

public class JoystickAnalog extends Component {
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
     */
    private final SimpleButton push;
    /**
     * normalized center position
     */
    private final double NORMALIZED_CENTER_POSITION = 0.5;
    /**
     * offset center x-axis
     */
    private double xOffset = 0.0;
    /**
     * offset center y-axis
     */
    private double yOffset = 0.0;
    /**
     * if true normalized axis from 0 to 1 center is 0.5, if false normalized axis from -1 to 1 center is 0
     */
    private final boolean normalized0to1;

    /**
     * Builds a new JoystickAnalog component with custom input for x-, y-axis, custom pin for push button.
     * ads component needs to be created outside this clas, other channels may be used for other components.
     *
     * @param pi4j        Pi4J context
     * @param ads1115     ads object
     * @param chanelXAxis analog potentiometer x-axis
     * @param chanelYAxis analog potentiometer y-axis
     * @param maxVoltage  max voltage expects on analog input x- and y-axis
     * @param push        additional push button on joystick
     */
    public JoystickAnalog(Context pi4j, ADS1115 ads1115, int chanelXAxis, int chanelYAxis, double maxVoltage, boolean normalized0to1, PIN push) {
        this.x = new Potentiometer(ads1115, chanelXAxis, maxVoltage);
        this.y = new Potentiometer(ads1115, chanelYAxis, maxVoltage);
        this.push = new SimpleButton(pi4j, push, true);
        this.normalized0to1 = normalized0to1;
    }

    /**
     * Builds a new JoystickAnalog component with default configuration for raspberry pi with ads1115 object
     *
     * @param pi4j    Pi4J context
     * @param ads1115 ads object
     * @param push    additional push button on joystick
     */
    public JoystickAnalog(Context pi4j, ADS1115 ads1115, PIN push) {
        this.x = new Potentiometer(ads1115, 0, 3.3);
        this.y = new Potentiometer(ads1115, 1, 3.3);
        this.push = new SimpleButton(pi4j, push, true);
        normalized0to1 = true;
    }

    /**
     * Sets or disables the handler for a value change event.
     * This event gets triggered whenever the x-axis of the joystick is moving.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void xOnMove(Runnable task) {
        x.setRunnableSlowReadChan(task);
    }

    /**
     * Sets or disables the handler for a value change event.
     * This event gets triggered whenever the y-axis of the joystick is moving.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void yOnMove(Runnable task) {
        y.setRunnableSlowReadChan(task);
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void pushOnDown(Runnable task) {
        push.onDown(task);
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void pushOnUp(Runnable task) {
        push.onUp(task);
    }

    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    public void pushWhilePressed(Runnable task, long whilePressedDelay) {
        push.whilePressed(task, whilePressedDelay);
    }

    /**
     * Returns the normalized value form 0 to 1 for x direction.
     *
     * @return normalized value
     */
    public double getXValue() {
        double result = x.continiousReadingGetNormalizedValue() + xOffset;

        result = Math.max(result, 0.0);
        result = Math.min(result, 1.0);

        if (!normalized0to1) {
            result = rescaleValue(result);
        }
        return result;
    }

    /**
     * Returns the normalized value form 0 to 1 for y direction.
     *
     * @return normalized value
     */
    public double getYValue() {
        double result = y.continiousReadingGetNormalizedValue() + yOffset;

        result = Math.max(result, 0.0);
        result = Math.min(result, 1.0);

        if (!normalized0to1) {
            result = rescaleValue(result);
        }

        return result;
    }

    /**
     * Start reading of joystick value. Needs to be triggered before any value can be read.
     *
     * @param threshold     delta between old and new value to trigger new event (+- voltage)
     * @param readFrequency update frequency to read new value from ad converter
     */
    public void start(double threshold, int readFrequency) {
        x.startSlowContiniousReading(threshold, readFrequency);
        y.startSlowContiniousReading(threshold, readFrequency);
    }

    /**
     * Stop reading of joystick value. If triggered no new value from joystick can be read.
     */
    public void stop() {
        x.stopSlowContiniousReading();
        y.stopSlowContiniousReading();
    }

    /**
     * disables all the handlers on joystick events
     */
    public void deregisterAll() {
        x.deregisterAll();
        y.deregisterAll();
        push.deRegisterAll();
    }

    /**
     * calibrates the center position of the joystick
     */
    public void calibrateJoystick() {
        xOffset = NORMALIZED_CENTER_POSITION - x.singleShotGetNormalizedValue();
        yOffset = NORMALIZED_CENTER_POSITION - y.singleShotGetNormalizedValue();
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
