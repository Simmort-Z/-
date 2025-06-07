package com.subway.service;

import com.subway.model.Line;
import com.subway.model.Station;
import com.subway.util.StationNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SubwaySystem {
    private Map<String, Station> stations;
    private Map<String, Line> lines;

    public SubwaySystem() {
        this.stations = new HashMap<>();
        this.lines = new HashMap<>();
    }

    public void addLine(Line line) {
        lines.put(line.getName(), line);
    }

    public void addStation(String stationName, String lineName) {
        Station station = stations.computeIfAbsent(stationName, Station::new);
        station.addLine(lineName);
    }

    public void addConnection(String station1Name, String station2Name, double distance, String lineName) {
        Station station1 = stations.get(station1Name);
        Station station2 = stations.get(station2Name);
        
        if (station1 != null && station2 != null) {
            station1.addAdjacentStation(station2, distance);
            station2.addAdjacentStation(station1, distance);
        }
    }

    // 1. 找出所有中转站
    public Map<String, Set<String>> findTransferStations() {
        Map<String, Set<String>> transferStations = new HashMap<>();
        
        for (Station station : stations.values()) {
            if (station.getLines().size() >= 2) {
                transferStations.put(station.getName(), new HashSet<>(station.getLines()));
            }
        }
        
        return transferStations;
    }

    // 2. 查找附近站点
    public Map<String, Map<String, Double>> findNearbyStations(String stationName, double maxDistance) 
            throws StationNotFoundException {
        
        if (!stations.containsKey(stationName)) {
            throw new StationNotFoundException("站点不存在: " + stationName);
        }
        
        Map<String, Map<String, Double>> result = new HashMap<>();
        Station startStation = stations.get(stationName);
        
        // BFS算法
        Queue<Station> queue = new LinkedList<>();
        Map<Station, Double> distances = new HashMap<>();
        
        queue.add(startStation);
        distances.put(startStation, 0.0);
        
        while (!queue.isEmpty()) {
            Station current = queue.poll();
            double currentDistance = distances.get(current);
            
            for (Map.Entry<Station, Double> entry : current.getAdjacentStations().entrySet()) {
                Station neighbor = entry.getKey();
                double edgeDistance = entry.getValue();
                double totalDistance = currentDistance + edgeDistance;
                
                if (totalDistance <= maxDistance && 
                    (!distances.containsKey(neighbor) || totalDistance < distances.get(neighbor))) {
                    
                    distances.put(neighbor, totalDistance);
                    queue.add(neighbor);
                }
            }
        }
        
        // 组织结果
        for (Map.Entry<Station, Double> entry : distances.entrySet()) {
            if (entry.getValue() > 0) { // 排除起点本身
                Station s = entry.getKey();
                for (String line : s.getLines()) {
                    if (!result.containsKey(s.getName())) {
                        result.put(s.getName(), new HashMap<>());
                    }
                    result.get(s.getName()).put(line, entry.getValue());
                }
            }
        }
        
        return result;
    }

    // 3. 查找所有路径
    public List<List<String>> findAllPaths(String start, String end) throws StationNotFoundException {
        if (!stations.containsKey(start) || !stations.containsKey(end)) {
            throw new StationNotFoundException("起点或终点不存在");
        }
        
        List<List<String>> paths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Station startStation = stations.get(start);
        Station endStation = stations.get(end);
        
        dfsFindPaths(startStation, endStation, visited, new ArrayList<>(), paths);
        
        return paths;
    }

    private void dfsFindPaths(Station current, Station end, Set<String> visited, 
                             List<String> currentPath, List<List<String>> paths) {
        
        visited.add(current.getName());
        currentPath.add(current.getName());
        
        if (current.equals(end)) {
            paths.add(new ArrayList<>(currentPath));
        } else {
            for (Station neighbor : current.getAdjacentStations().keySet()) {
                if (!visited.contains(neighbor.getName())) {
                    dfsFindPaths(neighbor, end, visited, currentPath, paths);
                }
            }
        }
        
        visited.remove(current.getName());
        currentPath.remove(currentPath.size() - 1);
    }
    
    public Map<String, Station> getStations() {
        return stations;
    }
}