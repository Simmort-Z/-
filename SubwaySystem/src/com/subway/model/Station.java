package com.subway.model;

import java.util.*;

public class Station {
    private final String name;
    private final Set<String> lines;
    private final Map<Station, Double> adjacentStations;

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

    // Getters
    public String getName() { return name; }
    public Set<String> getLines() { return Collections.unmodifiableSet(lines); }
    public Map<Station, Double> getAdjacentStations() { 
        return Collections.unmodifiableMap(adjacentStations); 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}