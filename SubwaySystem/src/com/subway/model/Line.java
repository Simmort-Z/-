package com.subway.model;

import java.util.*;

public class Line {
    private final String name;
    private final List<Station> stations;

    public Line(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
    }

    public void addStation(Station station) {
        if (!stations.contains(station)) {
            stations.add(station);
            station.addLine(this.name);
        }
    }

    // Getters
    public String getName() { return name; }
    public List<Station> getStations() { return Collections.unmodifiableList(stations); }
}