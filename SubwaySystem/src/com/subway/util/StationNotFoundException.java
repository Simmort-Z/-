package com.subway.util;

public class StationNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;  // 添加这一行
    
    public StationNotFoundException(String message) {
        super(message);
    }
}