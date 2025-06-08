package com.subway.model;

public class PriceInfo {
    private final double singleTicket;
    private final double wuhanPass;
    private final double dailyTicket;
    
    public PriceInfo(double singleTicket, double wuhanPass, double dailyTicket) {
        this.singleTicket = singleTicket;
        this.wuhanPass = wuhanPass;
        this.dailyTicket = dailyTicket;
    }
    
    // Getters
    public double getSingleTicket() { return singleTicket; }
    public double getWuhanPass() { return wuhanPass; }
    public double getDailyTicket() { return dailyTicket; }
}