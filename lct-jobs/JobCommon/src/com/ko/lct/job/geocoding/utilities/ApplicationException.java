package com.ko.lct.job.geocoding.utilities;

public class ApplicationException extends Exception {
    private static final long serialVersionUID = 8940241140437764301L;

    public ApplicationException(String message) {
	super(message);
    }

    public ApplicationException(Throwable cause) {
	super(cause);
    }

    public ApplicationException(String message, Throwable cause) {
	super(message, cause);
    }

}
