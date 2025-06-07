package com.subway.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Station {
    private String name;
    private Set<String> lines;
    private Map<Station, Double> adjacentStations; // 相邻站点及距离

    public Station(String name) {
        this.name = name;
        this.lines = new HashSet<>();
        this.adjacentStations = new HashMap<>();
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void addAdjacentStation(Station station, double distance) {
        adjacentStations.put(station, distance);
    }

    // Getter方法
    public String getName() { return name; }
    public Set<String> getLines() { return lines; }
    public Map<Station, Double> getAdjacentStations() { return adjacentStations; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Station station = (Station) obj;
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}