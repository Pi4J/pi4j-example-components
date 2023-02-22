package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.interfaces.Button;
import com.pi4j.io.gpio.digital.DigitalState;

public class McpButton extends Component implements Button{
    protected final Context pi4j;
    /**
     * Default debounce time in microseconds
     */
    protected static final long DEFAULT_DEBOUNCE = 10000;

    /**
     * Pi4J digital input instance used by this component
     */
    protected final Mcp23017.DigitalInOut PIN;
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
    private long whilePressedDelay;
    /**
     * Runnable Code when button is depressed
     */
    private Runnable onUp;

    private boolean oldState;

    private Thread interruptor;

    private long debounce;

    /**
     * Creates a new button component
     *
     * @param pi4j Pi4J context
     */
    public McpButton(Context pi4j, Mcp23017.DigitalInOut PIN, boolean inverted) {
        this(pi4j, PIN, inverted, DEFAULT_DEBOUNCE);
    }

    public McpButton(Context pi4j, Mcp23017.DigitalInOut PIN, boolean inverted, long debounce) {
        this.pi4j = pi4j;
        this.inverted = inverted;
        this.PIN = PIN;
        this.debounce = debounce;

        this.PIN.setDirection(Mcp23017.DigitalInOut.Direction.INPUT);
        this.PIN.setPullup(true);
        //invertion is made in button class, not hardware level
        this.PIN.invert_polarity(false);

        /*
         * Gets a DigitalStateChangeEvent directly from the Provider, as this
         * Class is a listener. This runs in a different Thread than main.
         * Calls the methods onUp, onDown and whilePressed. WhilePressed gets
         * executed in an own Thread, as to not block other resources.
         */
        interruptor = new Thread(() -> {
            while (true) {
                var currentState = getState();

                StateDirection direction;
                if (oldState != currentState) {
                    if (oldState) {
                        direction = StateDirection.HIGH_TO_LOW;
                    } else {
                        direction = StateDirection.LOW_TO_HIGH;
                    }

                    switch (direction) {
                        case LOW_TO_HIGH -> {
                            if (onDown != null) {
                                onDown.run();
                            }
                            if (whilePressed != null) {
                                new Thread(() -> {
                                    while (isDown()) {
                                        delay(whilePressedDelay);
                                        if (isDown()) {
                                            whilePressed.run();
                                        }
                                    }
                                }).start();
                            }
                        }
                        case HIGH_TO_LOW -> {
                            if (onUp != null) {
                                onUp.run();
                            }
                        }
                    }

                    oldState = currentState;
                }
                delay(debounce);
            }
        });

        interruptor.start();
    }

    /**
     * Returns the current state of the Digital State
     *
     * @return Current DigitalInput state (Can be HIGH, LOW or UNKNOWN)
     */
    public boolean getState() {
        return inverted == PIN.value();
    }

    /**
     * Checks if button is currently pressed
     *
     * @return True if button is pressed
     */
    @Override
    public boolean isDown() {
        return getState() == true;
    }

    /**
     * Checks if button is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    @Override
    public boolean isUp() {
        return getState() == false;
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    @Override
    public void onUp(Runnable task) {
        this.onUp = task;
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    @Override
    public void onDown(Runnable task) {
        this.onDown = task;
    }

    /**
     * Sets or disables the handler for the whilePressed event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param task Event handler to call or null to disable
     */
    @Override
    public void whilePressed(Runnable task, long whilePressedDelay) {
        this.whilePressed = task;
        this.whilePressedDelay = whilePressedDelay;
    }

    /**
     * disables all the handlers for the onUp, onDown and WhilePressed Events
     */
    @Override
    public void deRegisterAll() {
        this.onDown = null;
        this.onUp = null;
        this.whilePressed = null;
        try {
            this.interruptor.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the methode for OnDown
     * @return Runnable onDown
     */
    @Override
    public Runnable getOnDown() {
        return this.onDown;
    }

    /**
     * Returns the methode for OnUp
     * @return Runnable onUp
     */
    @Override
    public Runnable getOnUp() {
        return this.onUp;
    }

    /**
     * Returns the methode for whilePressed
     * @return Runnable whilePressed
     */
    @Override
    public Runnable getWhilePressed() {
        return this.whilePressed;
    }

    public enum StateDirection{
        HIGH_TO_LOW,
        LOW_TO_HIGH,
        NO_CHANGE
    }
}
