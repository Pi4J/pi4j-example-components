package com.pi4j.catalog.components;

import java.time.Duration;

import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;

import com.pi4j.catalog.components.base.PIN;
import com.pi4j.catalog.components.base.PwmActuator;

public class ServoMotor extends PwmActuator {
    /**
     * Default PWM frequency of the servo, based on values for SG92R
     */
    protected final static int DEFAULT_FREQUENCY = 50;

    /**
     * Default minimum angle of the servo motor, based on values for SG92R
     */
    protected final static float DEFAULT_MIN_ANGLE = -90;
    /**
     * Default maximum angle of the servo motor, based on values for SG92R
     */
    protected static final float DEFAULT_MAX_ANGLE = 90;

    /**
     * Default minimum PWM duty cycle to put the PWM into the minimum angle position
     */
    protected final static float DEFAULT_MIN_DUTY_CYCLE = 2;
    /**
     * Maximum PWM duty cycle to put the PWM into the maximum angle position
     */
    protected final static float DEFAULT_MAX_DUTY_CYCLE = 12;

    /**
     * Minimum angle of the servo motor used for this instance, should match previously tested real world values
     */
    private final float minAngle;
    /**
     * Maximum angle of the servo motor used for this instance, should match previously tested real world values
     */
    private final float maxAngle;
    /**
     * Minimum duty cycle of the servo motor for this instance, should match previously tested real world values
     */
    private final float minDutyCycle;
    /**
     * Maximum duty cycle of the servo motor for this instance, should match previously tested real world values
     */
    private final float maxDutyCycle;

    /**
     * Minimum value for user-defined range, defaults to 0
     */
    private float minRange = 0;
    /**
     * Maximum value for user-defined range, defaults to 1
     */
    private float maxRange = 1;

    /**
     * Creates a new step motor component with the default pin, angle range as well as duty cycle range.
     *
     * @param pi4j Pi4J context
     * @param address      Custom BCM pin address
     */
    public ServoMotor(Context pi4j, PIN address) {
        this(pi4j, address, DEFAULT_MIN_ANGLE, DEFAULT_MAX_ANGLE, DEFAULT_MIN_DUTY_CYCLE, DEFAULT_MAX_DUTY_CYCLE);
    }

    /**
     * Creates a new step motor component with the default pin and frequency but customized angle and duty cycle values.
     *
     * @param pi4j         Pi4J context
     * @param address      Custom BCM pin address
     * @param minAngle     Minimum angle in degrees
     * @param maxAngle     Maximum angle in degrees
     * @param minDutyCycle Minimum duty cycle as float, between 0 and 100
     * @param maxDutyCycle Maximum duty cycle as float, between 0 and 100
     */
    public ServoMotor(Context pi4j, PIN address, float minAngle, float maxAngle, float minDutyCycle, float maxDutyCycle) {
        this(pi4j, address, DEFAULT_FREQUENCY, minAngle, maxAngle, minDutyCycle, maxDutyCycle);
    }

    /**
     * Creates a new step motor component with custom pin, frequency, angle range and duty cycle range values.
     *
     * @param pi4j         Pi4J context
     * @param address      Custom BCM pin address
     * @param frequency    Frequency used for PWM with servo
     * @param minAngle     Minimum angle in degrees
     * @param maxAngle     Maximum angle in degrees
     * @param minDutyCycle Minimum duty cycle as float, between 0 and 100
     * @param maxDutyCycle Maximum duty cycle as float, between 0 and 100
     */
    public ServoMotor(Context pi4j, PIN address, int frequency, float minAngle, float maxAngle, float minDutyCycle, float maxDutyCycle) {
        super(pi4j,
                Pwm.newConfigBuilder(pi4j)
                .id("BCM-" + address)
                .name("Servo Motor " + address)
                .address(address.getPin())
                .pwmType(PwmType.HARDWARE)
                .frequency(frequency)
                .initial(0)
                .shutdown(0)
                .build());
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.minDutyCycle = minDutyCycle;
        this.maxDutyCycle = maxDutyCycle;
    }

    @Override
    public void reset() {
        setAngle(0);
        delay(Duration.ofSeconds(1));
        super.reset();
    }

    /**
     * Rotates the servo motor to the specified angle in degrees.
     * The angle should be between  {@link #getMinAngle()} and {@link #getMaxAngle()} which was specified during initialization.
     * Values outside this inclusive range are automatically being clamped to their respective minimum / maximum.
     *
     * @param angle New absolute angle
     */
    public void setAngle(float angle) {
        pwm.on(mapAngleToDutyCycle(angle));
    }

    /**
     * Rotates the servo by mapping a percentage value to the range between {@link #getMinAngle()} and {@link #getMaxAngle()}.
     * As an example, a value of 0% will equal to the minimum angle, 50% to the center and 100% to the maximum angle.
     *
     * @param percent Percentage value, automatically clamped between 0 and 100
     */
    public void setPercent(float percent) {
        moveOnRange(percent, 0, 100);
    }

    /**
     * Maps the given value based on the range previously defined with {@link #setRange(float, float)} to the full range of the servo.
     * If {@link #setRange(float, float)} was not called before, the default range of 0-1 (as float) is being used.
     *
     * @param value Value to map
     */
    public void moveOnRange(float value) {
        moveOnRange(value, minRange, maxRange);
    }

    /**
     * Maps the given value based on the given input range to the full range of the servo.
     * Unlike {@link #moveOnRange(float)}, this method will NOT use or adjust the values set by {@link #setRange(float, float)}.
     *
     * @param value Value to map
     * @param minValue Minimum range value
     * @param maxValue Maximum range value
     */
    public void moveOnRange(float value, float minValue, float maxValue) {
        pwm.on(mapToDutyCycle(value, minValue, maxValue));
    }

    /**
     * Adjusts the minimum and maximum for the user-defined range which can be used in combination with {@link #moveOnRange(float)}.
     * This method will only affect future calls to {@link #moveOnRange(float)} and does not change the current position.
     *
     * @param minValue Minimum range value
     * @param maxValue Maximum range value
     */
    public void setRange(float minValue, float maxValue) {
        this.minRange = minValue;
        this.maxRange = maxValue;
    }

    /**
     * Returns the minimum angle configured for this servo.
     *
     * @return Minimum angle in degrees
     */
    public float getMinAngle() {
        return minAngle;
    }

    /**
     * Returns the maximum angle configured for this servo.
     *
     * @return Maximum angle in degrees
     */
    public float getMaxAngle() {
        return maxAngle;
    }

    /**
     * Helper function to map an angle between {@link #minAngle} and {@link #maxAngle} to the configured duty cycle range.
     *
     * @param angle Desired angle
     * @return Duty cycle required to achieve this position
     */
    private float mapAngleToDutyCycle(float angle) {
        return mapToDutyCycle(angle, minAngle, maxAngle);
    }

    /**
     * Helper function to map an input value between a specified range to the configured duty cycle range.
     *
     * @param input      Value to map
     * @param inputStart Minimum value for custom range
     * @param inputEnd   Maximum value for custom range
     * @return Duty cycle required to achieve this position
     */
    private float mapToDutyCycle(float input, float inputStart, float inputEnd) {
        return mapRange(input, inputStart, inputEnd, minDutyCycle, maxDutyCycle);
    }

    /**
     * Helper function to map an input value from its input range to a possibly different output range.
     *
     * @param input       Input value to map
     * @param inputStart  Minimum value for input
     * @param inputEnd    Maximum value for input
     * @param outputStart Minimum value for output
     * @param outputEnd   Maximum value for output
     * @return Mapped input value
     */
    private float mapRange(float input, float inputStart, float inputEnd, float outputStart, float outputEnd) {
        // Automatically swap minimum/maximum of input if inverted
        if (inputStart > inputEnd) {
            final float tmp = inputEnd;
            inputEnd = inputStart;
            inputStart = tmp;
        }

        // Automatically swap minimum/maximum of output if inverted
        if (outputStart > outputEnd) {
            final float tmp = outputEnd;
            outputEnd = outputStart;
            outputStart = tmp;
        }

        // Automatically clamp the input value and calculate the mapped value
        final float clampedInput = Math.min(inputEnd, Math.max(inputStart, input));

        //this set the max to the left and min to the right
        //return outputStart + ((outputEnd - outputStart) / (inputEnd - inputStart)) * (clampedInput - inputStart);

        return outputEnd - ((outputEnd - outputStart) / (inputEnd - inputStart)) * (clampedInput - inputStart);
    }

}
