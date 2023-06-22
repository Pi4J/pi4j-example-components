package com.pi4j.catalog.components.base;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public abstract class Component {
    /**
     * Logger instance
     */
    private final Logger logger;

    protected Component(){
        logger = LogManager.getLogManager().getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

        //set appropriate log level
        logger.setLevel(Level.INFO);
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
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
