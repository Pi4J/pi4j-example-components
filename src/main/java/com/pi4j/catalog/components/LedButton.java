package com.pi4j.catalog.components;

import com.pi4j.context.Context;
import com.pi4j.catalog.components.helpers.PIN;
import com.pi4j.io.gpio.digital.*;

/**
 * Implementation of a button using GPIO with Pi4J
 */
public class LedButton extends Component {
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
    private final SimpleLed led;

    /**
     * Creates a new button component
     *
     * @param pi4j   Pi4J context
     * @param buttonAddress  GPIO address of button
     * @param inverted Specify if button state is inverted
     * @param ledAddress  GPIO address of LED
     */
    public LedButton(Context pi4j, PIN buttonAddress, Boolean inverted, PIN ledAddress) {
        this(pi4j, buttonAddress, inverted, ledAddress, DEFAULT_DEBOUNCE);
    }

    /**
     * Creates a new button component with custom GPIO address and debounce time.
     *
     * @param pi4j     Pi4J context
     * @param buttonAddress  GPIO address of button
     * @param inverted Specify if button state is inverted
     * @param ledAddress  GPIO address of LED
     * @param debounce Debounce time in microseconds
     */
    public LedButton(Context pi4j, PIN buttonAddress, boolean inverted, PIN ledAddress, long debounce) {
        this.button = new SimpleButton(pi4j, buttonAddress, inverted, debounce);
        this.led    = new SimpleLed(pi4j, ledAddress);
    }

    /**
     * Set the LED on or off depending on the boolean argument.
     *
     * @param on Sets the LED to on (true) or off (false)
     */
    public void ledSetState(boolean on) {
        led.setState(on);
    }

    /**
     * Sets the LED to on.
     */
    public void ledOn() {
        led.on();
    }

    /**
     * Sets the LED to off
     */
    public void ledOff() {
        led.off();
    }

    /**
     * Toggle the LED state depending on its current state.
     *
     * @return Return true or false according to the new state of the relay.
     */
    public boolean ledToggleState() {
        return led.toggleState();
    }

    /**
     * Returns the instance of the digital output
     *
     * @return DigitalOutput instance of the LED
     */
    public DigitalOutput ledGetDigitalOutput() {
        return led.getDigitalOutput();
    }

    /**
     * Returns the current state of the Digital State
     *
     * @return Current DigitalInput state (Can be HIGH, LOW or UNKNOWN)
     */
    public DigitalState btnGetState() { return button.getState(); }

    /**
     * Checks if button is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean btnIsDown() {
        return button.isDown();
    }

    /**
     * Checks if button is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean btnIsUp() {
        return button.isUp();
    }

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput btnGetDigitalInput() {
        return button.getDigitalInput();
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void btnOnDown(Runnable method) { button.onDown(method); }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void btnOnUp(Runnable method) {
        button.onUp(method);
    }
    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void btnWhilePressed(Runnable method, long millis) {button.whilePressed(method, millis); }

    /**
     * disables all the handlers for the onUp, onDown and whilePressed Events
     */
    public void btnDeRegisterAll(){ button.deRegisterAll(); }

    /**
     * @return the current Runnable that is set
     */
    public Runnable btnGetOnUp(){return button.getOnUp();}

    /**
     * @return the current Runnable that is set
     */
    public Runnable btnGetOnDown(){return button.getOnDown();}

    /**
     * @return the current Runnable that is set
     */
    public Runnable btnGetWhilePressed(){return button.getWhilePressed();}
}