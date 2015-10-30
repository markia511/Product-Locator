package com.ko.lct.ws.exception;

public class LocatorException extends Exception {
    private static final long serialVersionUID = 455024632823568117L;

    public LocatorException() {
    }

    /**
     * @param message
     */
    public LocatorException(String message) {
	super(message);
    }

    /**
     * @param cause
     */
    public LocatorException(Throwable cause) {
	super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public LocatorException(String message, Throwable cause) {
	super(message, cause);
    }

}
