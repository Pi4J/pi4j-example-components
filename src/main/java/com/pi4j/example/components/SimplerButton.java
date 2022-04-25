package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;

public class SimplerButton implements DigitalStateChangeListener {
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
     * Runnable Code when button is pressed
     */
    private Runnable onDown;
    /**
     * Handler while button is pressed
     */
    private Runnable whilePressed;
    /**
     * Timer while button is pressed
     */
    private long whilePressedDEBOUNCE;
    /**
     * Runnable Code when button is depressed
     */
    private Runnable onUp;
    /**
     * Creates a new button component
     *
     * @param pi4j   Pi4J context
     */
    public SimplerButton(Context pi4j, int address, Boolean inverted) {
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
    public SimplerButton(Context pi4j, int address, boolean inverted, long debounce) {
        this.inverted = inverted;
        this.digitalInput = pi4j.create(buildDigitalInputConfig(pi4j, address, inverted, debounce));
        this.digitalInput.addListener(this);
    }

    /**
     * Returns the current state of the Digital State
     *
     * @return Current DigitalInput state (Can be HIGH, LOW or UNKNOWN)
     */
    public DigitalState getState() {
        return switch (digitalInput.state()) {
            case HIGH -> inverted ? DigitalState.LOW : DigitalState.HIGH;
            case LOW -> inverted ? DigitalState.HIGH : DigitalState.LOW;
            default -> DigitalState.UNKNOWN;
        };
    }

    /**
     * Checks if button is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean isDown() {
        return getState() == DigitalState.HIGH;
    }

    /**
     * Checks if button is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean isUp() {
        return getState() == DigitalState.LOW;
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

    @Override
    public void onDigitalStateChange(DigitalStateChangeEvent digitalStateChangeEvent) {
        DigitalState state = inverted ? getState() : digitalStateChangeEvent.state();
        switch (state){
            case HIGH:
                this.onDown.run();
                new Thread(() -> {
                    while(whilePressed != null && isDown()){
                        whilePressed.run();
                        try {
                            Thread.sleep(whilePressedDEBOUNCE);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    return;
                }).start();
                return;
            case LOW:
                this.onUp.run();
                return;
            default:
                return;
        }
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void onDown(Runnable method) {
        this.onDown = method;
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void onUp(Runnable method) {
        this.onUp = method;
    }
    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param method Event handler to call or null to disable
     */
    public void whilePressed(long whilePressedDEBOUNCE, Runnable method) {
        this.whilePressed = method;
        this.whilePressedDEBOUNCE = whilePressedDEBOUNCE;
    }
}
