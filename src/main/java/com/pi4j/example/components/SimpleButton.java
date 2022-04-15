package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.events.DigitalEventProvider;
import com.pi4j.example.components.events.EventHandler;
import com.pi4j.example.helpers.SimpleInput;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

/**
 * Implementation of a button using GPIO with Pi4J
 */
public class SimpleButton extends Component implements DigitalEventProvider<SimpleButton.ButtonState>, SimpleInput {
    /**
     * Default debounce time in microseconds
     */
    protected static final long DEFAULT_DEBOUNCE = 10000;

    /**
     * Pi4J digital input instance used by this component
     */
    protected final DigitalInput digitalInput;
    /**
     * Specifies if button state is inverted, e.g. HIGH = depressed, LOW = pressed
     * This will also automatically switch the pull resistance to PULL_UP
     */
    private final boolean inverted;

    /**
     * Handler for simple event when button is pressed
     */
    private EventHandler onDown;
    /**
     * Handler for simple event when button is depressed
     */
    private EventHandler onUp;
    /**
     * Handler for simple event when button is pressed
     */
    private EventHandler whilePressed;

    /**
     * Creates a new button component
     *
     * @param pi4j   Pi4J context
     */
    public SimpleButton(Context pi4j, int address, Boolean inverted) {
        this(pi4j, address, inverted, DEFAULT_DEBOUNCE);
    }

    /**
     * Creates a new button component with custom GPIO address and debounce time.
     *
     * @param pi4j     Pi4J context
     * @param address  GPIO address of button
     * @param inverted Specify if button state is inverted
     * @param debounce Debounce time in microseconds
     */
    public SimpleButton(Context pi4j, int address, boolean inverted, long debounce) {
        this.inverted = inverted;
        this.digitalInput = pi4j.create(buildDigitalInputConfig(pi4j, address, inverted, debounce));
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
                triggerEvent(onDown);
                while (isDown()) {
                    triggerEvent(whilePressed);
                    sleep(DEFAULT_DEBOUNCE/1000);
                }
                break;
            case UP:
                triggerEvent(onUp);
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
    public void onDown(EventHandler handler) {
        this.onDown = handler;
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void onUp(EventHandler handler) {
        this.onUp = handler;
    }

    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is being pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void whilePressed(EventHandler handler) {
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
     * @param address  GPIO address of button component
     * @param inverted Specify if button state is inverted
     * @param debounce Debounce time in microseconds
     * @return DigitalInput configuration
     */
    private DigitalInputConfig buildDigitalInputConfig(Context pi4j, int address, boolean inverted, long debounce) {
        return DigitalInput.newConfigBuilder(pi4j)
                .id("BCM" + address)
                .name("Button #" + address)
                .address(address)
                .debounce(debounce)
                .pull(inverted ? PullResistance.PULL_UP : PullResistance.PULL_DOWN)
                .build();
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