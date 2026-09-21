package com.mednet.externalpacs.hl7;

public class Hl7ParseException extends Exception {

    public Hl7ParseException(String message) {
        super(message);
    }

    public Hl7ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
