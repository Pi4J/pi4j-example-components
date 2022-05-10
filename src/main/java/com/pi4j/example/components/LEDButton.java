package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;

/**
 * Implementation of a button using GPIO with Pi4J
 */
public class LEDButton extends Component {
    /**
     * Default debounce time in microseconds
     */
    protected static final long DEFAULT_DEBOUNCE = 10000;

    /**
     * Button component
     */
    private final SimpleButton button;
    /**
     * LED component
     */
    private final SimpleLED led;

    /**
     * Creates a new button component
     *
     * @param pi4j   Pi4J context
     * @param buttonaddress  GPIO address of button
     * @param inverted Specify if button state is inverted
     * @param ledaddress  GPIO address of LED
     */
    public LEDButton(Context pi4j, PIN buttonaddress, Boolean inverted, PIN ledaddress) {
        this(pi4j, buttonaddress, inverted, ledaddress, DEFAULT_DEBOUNCE);
    }

    /**
     * Creates a new button component with custom GPIO address and debounce time.
     *
     * @param pi4j     Pi4J context
     * @param buttonaddress  GPIO address of button
     * @param inverted Specify if button state is inverted
     * @param ledaddress  GPIO address of LED
     * @param debounce Debounce time in microseconds
     */
    public LEDButton(Context pi4j, PIN buttonaddress, boolean inverted, PIN ledaddress, long debounce) {
        this.button = new SimpleButton(pi4j, buttonaddress, inverted, debounce);
        this.led = new SimpleLED(pi4j, ledaddress);
    }

    /**
     * Set the LED on or off depending on the boolean argument.
     *
     * @param on Sets the LED to on (true) or off (false)
     */
    public void LEDsetState(boolean on) {
        led.setState(on);
    }

    /**
     * Sets the LED to on.
     */
    public void LEDsetStateOn() {
        led.setStateOn();
    }

    /**
     * Sets the LED to off
     */
    public void LEDsetStateOff() {
        led.setStateOff();
    }

    /**
     * Toggle the LED state depending on its current state.
     *
     * @return Return true or false according to the new state of the relay.
     */
    public boolean LEDtoggleState() {
        return led.toggleState();
    }

    /**
     * Returns the instance of the digital output
     *
     * @return DigitalOutput instance of the LED
     */
    public DigitalOutput LEDgetDigitalOutput() {
        return led.getDigitalOutput();
    }

    /**
     * Returns the current state of the Digital State
     *
     * @return Current DigitalInput state (Can be HIGH, LOW or UNKNOWN)
     */
    public DigitalState btngetState() { return button.getState(); }

    /**
     * Checks if button is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean btnisDown() {
        return button.isDown();
    }

    /**
     * Checks if button is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean btnisUp() {
        return button.isUp();
    }

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput btngetDigitalInput() {
        return button.getDigitalInput();
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void onDown(Runnable method) { button.onDown(method); }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void onUp(Runnable method) {
        button.onUp(method);
    }
    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void btnwhilePressed(long millis, Runnable method) {button.whilePressed(method, millis); }

    /**
     * disables all the handlers for the onUp, onDown and WhilePressed Events
     */
    public void btndeRegisterAll(){ button.deRegisterAll(); }
}