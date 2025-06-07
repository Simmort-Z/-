package com.subway.model;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private String name;
    private List<Station> stations;

    public Line(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
    }

    public void addStation(Station station) {
        stations.add(station);
        station.addLine(this.name);
    }

    // Getter方法
    public String getName() { return name; }
    public List<Station> getStations() { return stations; }
}