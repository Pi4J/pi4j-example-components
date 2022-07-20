package com.pi4j.components.components.helpers;

public class InstanceAlreadyRunningException extends RuntimeException {
	public InstanceAlreadyRunningException(String message) {
		super(message);
	}
	public InstanceAlreadyRunningException(String message, Throwable cause) {
		super(message, cause);
	}
}
