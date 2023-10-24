package com.pi4j.catalog.components;

import java.time.Duration;

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
 *
 */
public class JoystickAnalog extends Component {

    private final Ads1115 ads1115;
    /**
     * potentiometer x axis
     */
    private final Potentiometer xAxis;
    /**
     * potentiometer y axis
     */
    private final Potentiometer yAxis;
    /**
     * button push
     *
     * Can be 'null' if joystick doesn't have button functionality or if you don't want to use it
     */
    private final SimpleButton push;

    private final double normThreshold;

    private double xActualValue;
    private double yActualValue;

    private double xLastNotifiedValue = 999;
    private double yLastNotifiedValue = 999;


    /**
     * Builds a new JoystickAnalog component with custom input for x-, y-axis, custom pin for push button.
     * ads component needs to be created outside this clas, other channels may be used for other components.
     *
     * @param ads1115      ads object
     * @param channelXAxis analog potentiometer x-axis
     * @param channelYAxis analog potentiometer y-axis
     * @param pin          additional push button on joystick
     */
    public JoystickAnalog(Ads1115 ads1115, Ads1115.Channel channelXAxis, Ads1115.Channel channelYAxis, PIN pin, boolean inverted) {
        this(ads1115,
             new Potentiometer(ads1115, channelXAxis, Potentiometer.Range.MINUS_ONE_TO_ONE),
             new Potentiometer(ads1115, channelYAxis, Potentiometer.Range.MINUS_ONE_TO_ONE),
             0.05,
             pin != null ? new SimpleButton(ads1115.getPi4j(), pin, inverted) : null);
    }

    /**
     * @param potentiometerX potentiometer object for x-axis
     * @param potentiometerY potentiometer object for y-axis
     * @param push           simpleButton object for push button on joystick
     */
    JoystickAnalog(Ads1115 ads1115, Potentiometer potentiometerX, Potentiometer potentiometerY, double normThreshold, SimpleButton push) {
        this.ads1115       = ads1115;
        this.xAxis         = potentiometerX;
        this.yAxis         = potentiometerY;
        this.push          = push;
        this.normThreshold = normThreshold;
    }

    public void onMove(PositionConsumer onMove, Runnable onCenter){
        xAxis.onNormalizedValueChange((xPos) -> {
            xActualValue = xPos;
            notifyIfNeeded(onMove, onCenter);
        });

        yAxis.onNormalizedValueChange((yPos) -> {
            yActualValue = -yPos;
            notifyIfNeeded(onMove, onCenter);
        });
    }

    private synchronized void notifyIfNeeded(PositionConsumer onMove, Runnable onCenter){
        final double homeArea = 0.1;
        if (Math.abs(xActualValue) <= homeArea && Math.abs(yActualValue) <= homeArea) {
            xLastNotifiedValue = xActualValue;
            yLastNotifiedValue = yActualValue;
            if (onCenter != null) {
                onCenter.run();
            }
        }
        else {
            double distance = Math.sqrt(Math.pow(xActualValue - xLastNotifiedValue, 2) + Math.pow(yActualValue - yLastNotifiedValue, 2));

            if(distance > normThreshold){
                xLastNotifiedValue = xActualValue;
                yLastNotifiedValue = yActualValue;

                if(onMove != null){
                    onMove.accept(xLastNotifiedValue, yLastNotifiedValue);
                }
            }
        }
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
     * disables all the handlers on joystick events
     */
    @Override
    public void reset() {
        ads1115.stopContinuousReading();
        xAxis.reset();
        yAxis.reset();
        push.reset();
    }

    @FunctionalInterface
    public interface PositionConsumer{
        void accept(double xPos, double YPos);
    }

}
