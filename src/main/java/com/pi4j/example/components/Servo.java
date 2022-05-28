package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.helpers.PIN;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;

/**
 * FHNW implementation for controlling servo motors. This class allows its user to control a wide range of 50 hertz
 * servo motors. The position of the motor can either be moved to a specific angle by providing the desired degree
 * or it can be set to an exact pulse length.
 */
public class Servo extends Component {
    private final int pulseMin;
    private final int pulseMax;
    private final int maxDegrees;
    private final Pwm pwm;

    /**
     * Constructor with all possible values.
     *
     * @param pin        The hardware PWM pin
     * @param pulseMin   Minimal high time of the 50hz pulse in microseconds
     * @param pulseMax   Maximal high time of the 50hz pulse in microseconds
     * @param maxDegrees Maximal degrees the servo is able to spin
     */
    public Servo(Context pi4j, PIN pin, int pulseMin, int pulseMax, int maxDegrees) {
        this.pulseMin = pulseMin;
        this.pulseMax = pulseMax;
        this.maxDegrees = maxDegrees;

        this.pwm = pi4j.create(buildPwmConfig(pi4j, pin));
    }

    /**
     * Constructor with default value for maxDegrees of 180 degrees.
     *
     * @param pin      The hardware PWM pin
     * @param pulseMin Minimal high time of the 50hz pulse in microseconds
     * @param pulseMax Maximal high time of the 50hz pulse in microseconds
     */
    public Servo(Context pi4j, PIN pin, int pulseMin, int pulseMax) {
        this(pi4j,pin, pulseMin, pulseMax, 180);
    }

    /**
     * Constructor with default value for pulseMin = 500, pulseMax = 2400 and maxDegrees = 180. This constructor can
     * be used for most micro and mini servo motors.
     *
     * @param pin The hardware PWM pin
     */
    public Servo(Context pi4j, PIN pin) {
        this(pi4j, pin, 500, 2400, 180);
    }

    /**
     * Builds a new PWM configuration for the buzzer
     *
     * @param pi4j    Pi4J context
     * @param address BCM pin address
     * @return PWM    configuration
     */
    protected static PwmConfig buildPwmConfig(Context pi4j, PIN address) {
        return Pwm.newConfigBuilder(pi4j)
                .id("BCM" + address.getPin())
                .name("Servo")
                .address(address.getPin())
                .pwmType(PwmType.HARDWARE)
                .initial(0)
                .shutdown(0)
                .frequency(50)
                .build();
    }

    /**
     * Returns the created PWM instance for the buzzer
     *
     * @return PWM instance
     */
    protected Pwm getPwm() {
        return this.pwm;
    }

    /**
     * Sets the PWM value of the pin
     *
     * @param pwm The desired PWM value
     */
    private void setPwm(int pwm) {
        this.pwm.on(50, pwm);
        delay(15);
    }

    /**
     * Closes the Device
     */
    public void close() {
        this.pwm.off();
    }

    /**
     * Sets the servo position to a specific pulse high time in microseconds
     *
     * @param pulse The desired pulse length in microseconds
     * @throws IllegalArgumentException Thrown when a value smaller than pulseMin or bigger than pulse max is provided
     */
    public void setPosition(int pulse) throws IllegalArgumentException {
        if (pulse < pulseMin || pulse > pulseMax) {
            throw new IllegalArgumentException(
                    "ServoMotorComponent: Please provide a value in the range pulseMin to pulseMax");
        }

        setPwm(pulse);
    }

    /**
     * Sets the servo position to a desired angle in degrees
     *
     * @param degree The desired rotation in degrees
     * @throws IllegalArgumentException Thrown when a value smaller than 0 or bigger than maxDegrees is provided
     */
    public void setPositionDegrees(int degree) throws IllegalArgumentException {
        if (degree < 0 || degree > maxDegrees) {
            throw new IllegalArgumentException(
                    "ServoMotorComponent: Please provide a value in the range 0 to maxDegrees");
        }

        double stepSize = ((double) maxDegrees / (pulseMax - pulseMin));
        int pulse = pulseMin + (int) Math.round(degree / stepSize);
        setPwm(pulse);
    }

    /**
     * Sets the servo to its maximal position
     */
    public void setMax() {
        setPwm(pulseMax);
    }

    /**
     * Sets the servo to its center position
     */
    public void setCenter() {
        setPwm(pulseMin + Math.round((pulseMax - pulseMin) >> 1));
    }

    /**
     * Sets the servo to its minimal position
     */
    public void setMin() {
        setPwm(pulseMin);
    }
}
