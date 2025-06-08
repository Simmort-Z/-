package com.subway.util;

public class StationNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public StationNotFoundException(String message) {
        super(message);
    }
}