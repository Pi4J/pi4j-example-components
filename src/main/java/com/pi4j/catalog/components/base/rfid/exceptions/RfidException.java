package com.pi4j.catalog.components.base.rfid.exceptions;

import com.pi4j.catalog.components.base.rfid.PcdError;

/**
 * Base exception for indicating failures within the RFID component
 */
public class RfidException extends Exception {
    public RfidException(String message) {
        super(message);
    }

    public RfidException(String message, Throwable previous) {
        super(message, previous);
    }

    public RfidException(PcdError error) {
        super(error.getDescription());
    }
}

