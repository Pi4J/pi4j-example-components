package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.events.DigitalEventProvider;
import com.pi4j.example.components.events.EventHandler;
import com.pi4j.example.components.events.SimpleEventHandler;
import com.pi4j.example.helpers.SimpleInput;
import com.pi4j.example.helpers.SimpleOutput;
import com.pi4j.io.gpio.digital.*;

import java.util.function.ToDoubleBiFunction;

/**
 * Implementation of a button using GPIO with Pi4J
 */
public class LEDButton extends Component implements DigitalEventProvider<LEDButton.ButtonState>, SimpleInput, SimpleOutput {
    /**
     * Default debounce time in microseconds
     */
    protected static final long DEFAULT_DEBOUNCE = 10000;

    /**
     * Pi4J digital input instance used by this component
     */
    protected final DigitalInput digitalInput;

    /**
     * Pi4J digital output instance used by this component
     */
    protected final DigitalOutput digitalOutput;
    /**
     * Specifies if button state is inverted, e.g. HIGH = depressed, LOW = pressed
     * This will also automatically switch the pull resistance to PULL_UP
     */
    private final boolean inverted;

    /**
     * Handler for simple event when button is pressed
     */
    private SimpleEventHandler onDown;
    /**
     * Handler for simple event when button is depressed
     */
    private SimpleEventHandler onUp;

    /**
     * Handler for simple event when button is depressed
     */
    private SimpleEventHandler whilePressed;

    /**
     * Creates a new button component
     *
     * @param pi4j   Pi4J context
     * @param buttonaddress  GPIO address of button
     * @param inverted Specify if button state is inverted
     * @param ledaddress  GPIO address of LED
     */
    public LEDButton(Context pi4j, int buttonaddress, Boolean inverted, int ledaddress) {
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
    public LEDButton(Context pi4j, int buttonaddress, boolean inverted, int ledaddress, long debounce) {
        this.inverted = inverted;
        this.digitalInput = pi4j.create(buildDigitalInputConfig(pi4j, buttonaddress, inverted, debounce));
        this.digitalOutput = pi4j.create(buildDigitalOutputConfig(pi4j, ledaddress));
        this.addListener(this::dispatchSimpleEvents);
    }

    /**
     * Returns the current state of the Digital State
     *
     * @return Current button state
     */
    public ButtonState getState() {
        return mapDigitalState(digitalInput.state());
    }

    /**
     * Checks if button is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean isDown() {
        return getState() == ButtonState.DOWN;
    }

    /**
     * Checks if button is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean isUp() {
        return getState() == ButtonState.UP;
    }

    /**
     * Maps a {@link DigitalState} to a well-known {@link ButtonState}
     *
     * @param digitalState Pi4J digital state to map
     * @return Mapped touch state
     */
    public ButtonState mapDigitalState(DigitalState digitalState) {
        switch (digitalState) {
            case HIGH:
                return inverted ? ButtonState.UP : ButtonState.DOWN;
            case LOW:
                return inverted ? ButtonState.DOWN : ButtonState.UP;
            default:
                return ButtonState.UNKNOWN;
        }
    }

    /**
     * Analyzes the given value passed by an event and triggers 0-n simple events based on it.
     * This method allows mapping various value/state changes to simple events.
     *
     * @param state    Button state
     */
    public void dispatchSimpleEvents(ButtonState state) {
        switch (state) {
            case DOWN:
                triggerSimpleEvent(onDown);
                while (isDown()) {
                    triggerSimpleEvent(whilePressed);
                    sleep(DEFAULT_DEBOUNCE/1000);
                }
                break;
            case UP:
                triggerSimpleEvent(onUp);
                break;
        }
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void onDown(SimpleEventHandler handler) {
        this.onDown = handler;
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void onUp(SimpleEventHandler handler) {
        this.onUp = handler;
    }

    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is being pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void whilePressed(SimpleEventHandler handler) {
        this.whilePressed = handler;
    }

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput getDigitalInput() {
        return this.digitalInput;
    }

    /**
     * Builds a new DigitalInput configuration for the button component.
     *
     * @param pi4j     Pi4J context
     * @param buttonaddress  GPIO address of button component
     * @param inverted Specify if button state is inverted
     * @param debounce Debounce time in microseconds
     * @return DigitalInput configuration
     */
    private DigitalInputConfig buildDigitalInputConfig(Context pi4j, int buttonaddress, boolean inverted, long debounce) {
        return DigitalInput.newConfigBuilder(pi4j)
                .id("BCM" + buttonaddress)
                .name("Button #" + buttonaddress)
                .address(buttonaddress)
                .debounce(debounce)
                .pull(inverted ? PullResistance.PULL_UP : PullResistance.PULL_DOWN)
                .build();
    }

    /**
     * Configure Digital Input
     *
     * @param pi4j    PI4J Context
     * @param ledaddress GPIO Address of the relay
     * @return Return Digital Input configuration
     */
    protected DigitalOutputConfig buildDigitalOutputConfig(Context pi4j, int ledaddress) {
        return DigitalOutput.newConfigBuilder(pi4j)
                .id("BCM" + ledaddress)
                .name("LED")
                .address(ledaddress)
                .build();
    }

    /**
     * Set the LED on or off depending on the boolean argument.
     *
     * @param on Sets the LED to on (true) or off (false)
     */
    @Override
    public void setState(boolean on) {
        digitalOutput.setState(!on);
    }

    /**
     * Sets the LED to on.
     */
    @Override
    public void setStateOn() {
        digitalOutput.off();
    }

    /**
     * Sets the LED to off
     */
    @Override
    public void setStateOff() {
        digitalOutput.on();
    }

    /**
     * Toggle the LED state depending on its current state.
     *
     * @return Return true or false according to the new state of the relay.
     */
    @Override
    public boolean toggleState() {
        digitalOutput.toggle();
        return digitalOutput.isOff();
    }

    /**
     * Returns the instance of the digital output
     *
     * @return DigitalOutput instance of the LED
     */
    @Override
    public DigitalOutput getDigitalOutput() {
        return digitalOutput;
    }

    /**
     * All available states reported by the button component.
     * This enum is in the Component itself, as it only Represents this class itself
     */
    public enum ButtonState {
        DOWN,
        UP,
        UNKNOWN
    }
}