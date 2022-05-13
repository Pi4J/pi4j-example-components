package com.pi4j.example.helpers;

public class ContiniousMeasuringException extends RuntimeException{

    public ContiniousMeasuringException(String message) {
        super(message);
    }

    public ContiniousMeasuringException(String message, Throwable cause) {
        super(message, cause);
    }
}
