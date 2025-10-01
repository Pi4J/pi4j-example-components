package com.pi4j.catalog.components.base.rfid.exceptions;

/**
 * Generic class used for indicating measurement failures for various components
 */
public class MeasurementException extends RuntimeException {
    public MeasurementException(String message) {
        super(message);
    }
}

