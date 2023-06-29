package com.pi4j.catalog.components.base;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Component {
    /**
     * Logger instance
     */
    private static final Logger logger = Logger.getGlobal();

    protected Component(){
        Level appropriateLevel = Level.INFO;

        logger.setLevel(appropriateLevel);
        ConsoleHandler handler = new ConsoleHandler();

        handler.setLevel(appropriateLevel);
        logger.addHandler(handler);
    }

    /**
     * Override this method to clean up all used resources
     */
    public void reset(){
        //nothing to do by default
    }

    protected void logInfo(String msg, Object... args) {
        logger.info(() -> String.format(msg, args));
    }

    protected void logError(String msg, Object... args) {
        logger.severe(() -> String.format(msg, args));
    }

    protected void logConfig(String msg, Object... args) {
        logger.config(() -> String.format(msg, args));
    }

    protected void logDebug(String msg, Object... args) {
        logger.fine(() -> String.format(msg, args));
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be caught and ignored while setting the interrupt flag again.
     *
     * @param milliseconds Time in milliseconds to sleep
     */
    protected void delay(long milliseconds) {
        delay(milliseconds, 0);
    }

    protected void delay(long milliseconds, int nanoseconds){
        try {
            Thread.sleep(milliseconds, nanoseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected <T> T asMock(Class<T> type, Object instance) {
        return type.cast(instance);
    }
}
