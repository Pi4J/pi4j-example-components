package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.interfaces.Button;

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


    }

    /**
     * Returns the current state of the Digital State
     *
     * @return Current DigitalInput state (Can be HIGH, LOW or UNKNOWN)
     */
    public boolean getState() {
        return inverted == PIN.value();
    }

    @Override
    public boolean isDown() {
        return getState() == true;
    }

    @Override
    public boolean isUp() {
        return getState() == false;
    }

    @Override
    public void onUp(Runnable task) {
        this.onUp = task;
    }

    @Override
    public void onDown(Runnable task) {
        this.onDown = task;
    }

    @Override
    public void whilePressed(Runnable task) {
        this.whilePressed = task;
    }

    @Override
    public void deRegisterAll() {
        this.onDown = null;
        this.onUp = null;
        this.whilePressed = null;
    }

    @Override
    public Runnable getOnDown() {
        return this.onDown;
    }

    @Override
    public Runnable getOnUp() {
        return this.onUp;
    }

    @Override
    public Runnable getWhilePressed() {
        return this.whilePressed;
    }
}
