package com.subway.model;

import java.util.List;

public class PathResult {
    private final List<String> path;
    private final int transfers;
    private final double distance;
    
    public PathResult(List<String> path, int transfers, double distance) {
        this.path = path;
        this.transfers = transfers;
        this.distance = distance;
    }
    
    // Getters
    public List<String> getPath() { return path; }
    public int getTransfers() { return transfers; }
    public double getDistance() { return distance; }
}