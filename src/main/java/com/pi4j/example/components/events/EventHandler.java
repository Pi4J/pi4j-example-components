package com.pi4j.example.components.events;

/**
 * Generic functional interface for simple event handlers, which are event handlers without a parameter.
 * Usually supposed to be called / triggered within {@link EventProvider#dispatchSimpleEvents(Object)}
 */
@FunctionalInterface
public interface EventHandler {
    /**
     * Handles a specific simple event based on implementation needs.
     * This method does not take any parameters and returns no value either.
     * For more advanced event handling, use {@link ValueChangeHandler}.
     */
    void handle();
}
