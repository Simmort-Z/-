package com.subway.service;

import com.subway.model.*;
import com.subway.util.StationNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class SubwaySystem {
    private final Map<String, Station> stations;
    private final Map<String, Line> lines;
    
    public SubwaySystem() {
        this.stations = new HashMap<>();
        this.lines = new HashMap<>();
    }
    
    // 基本操作方法
    public void addLine(Line line) {
        lines.put(line.getName(), line);
    }
    
    public void addStation(String stationName, String lineName) {
        Station station = stations.computeIfAbsent(stationName, Station::new);
        station.addLine(lineName);
    }
    
    public void addConnection(String station1, String station2, double distance, String lineName) {
        Station s1 = stations.get(station1);
        Station s2 = stations.get(station2);
        if (s1 != null && s2 != null) {
            s1.addAdjacentStation(s2, distance);
            s2.addAdjacentStation(s1, distance);
        }
    }
    
    // 1. 识别中转站
    public Map<String, Set<String>> getTransferStations() {
        return stations.entrySet().stream()
            .filter(entry -> entry.getValue().getLines().size() > 1)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getLines()
            ));
    }
    
    // 2. 查找附近站点
    public Map<String, Map<String, Double>> findNearbyStations(String stationName, double maxDistance) 
            throws StationNotFoundException {
        if (!stations.containsKey(stationName)) {
            throw new StationNotFoundException("站点不存在: " + stationName);
        }
        
        Map<String, Map<String, Double>> result = new HashMap<>();
        Station start = stations.get(stationName);
        
        // BFS算法
        Queue<Station> queue = new LinkedList<>();
        Map<Station, Double> distances = new HashMap<>();
        
        queue.add(start);
        distances.put(start, 0.0);
        
        while (!queue.isEmpty()) {
            Station current = queue.poll();
            double currentDist = distances.get(current);
            
            for (Map.Entry<Station, Double> entry : current.getAdjacentStations().entrySet()) {
                Station neighbor = entry.getKey();
                double edgeDist = entry.getValue();
                double totalDist = currentDist + edgeDist;
                
                if (totalDist <= maxDistance && 
                    (!distances.containsKey(neighbor) || totalDist < distances.get(neighbor))) {
                    
                    distances.put(neighbor, totalDist);
                    queue.add(neighbor);
                }
            }
        }
        
        // 组织结果
        distances.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .forEach(entry -> {
                Station s = entry.getKey();
                s.getLines().forEach(line -> {
                    result.computeIfAbsent(s.getName(), k -> new HashMap<>())
                          .put(line, entry.getValue());
                });
            });
        
        return result;
    }
    
    // 3. 查找所有路径 (DFS)
    public List<List<String>> findAllPaths(String start, String end) throws StationNotFoundException {
        if (!stations.containsKey(start) || !stations.containsKey(end)) {
            throw new StationNotFoundException("起点或终点不存在");
        }
        
        List<List<String>> paths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsFindAllPaths(start, end, visited, new ArrayList<>(), paths);
        return paths;
    }
    
    private void dfsFindAllPaths(String current, String end, Set<String> visited, 
                               List<String> currentPath, List<List<String>> paths) {
        visited.add(current);
        currentPath.add(current);
        
        if (current.equals(end)) {
            paths.add(new ArrayList<>(currentPath));
        } else {
            Station station = stations.get(current);
            for (Station neighbor : station.getAdjacentStations().keySet()) {
                if (!visited.contains(neighbor.getName())) {
                    dfsFindAllPaths(neighbor.getName(), end, visited, currentPath, paths);
                }
            }
        }
        
        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }
    
    // 4. 查找最短路径 (Dijkstra)
    public PathResult findShortestPath(String start, String end) throws StationNotFoundException {
        if (!stations.containsKey(start) || !stations.containsKey(end)) {
            throw new StationNotFoundException("起点或终点不存在");
        }
        
        PriorityQueue<PathNode> queue = new PriorityQueue<>();
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Map<String, Integer> transfers = new HashMap<>();
        
        // 初始化
        for (String station : stations.keySet()) {
            if (station.equals(start)) {
                distances.put(station, 0.0);
                transfers.put(station, 0);
                queue.add(new PathNode(station, 0.0, 0));
            } else {
                distances.put(station, Double.MAX_VALUE);
                transfers.put(station, Integer.MAX_VALUE);
                queue.add(new PathNode(station, Double.MAX_VALUE, Integer.MAX_VALUE));
            }
            previous.put(station, null);
        }
        
        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            
            if (current.station.equals(end)) {
                break;
            }
            
            Station currentStation = stations.get(current.station);
            for (Map.Entry<Station, Double> neighbor : currentStation.getAdjacentStations().entrySet()) {
                String neighborName = neighbor.getKey().getName();
                double altDist = distances.get(current.station) + neighbor.getValue();
                
                // 计算换乘次数
                int altTransfers = transfers.get(current.station);
                if (previous.get(current.station) != null) {
                    String prevStation = previous.get(current.station);
                    Set<String> commonLines = new HashSet<>(stations.get(current.station).getLines());
                    commonLines.retainAll(stations.get(prevStation).getLines());
                    if (commonLines.isEmpty()) {
                        altTransfers++;
                    }
                }
                
                if (altDist < distances.get(neighborName) || 
                    (altDist == distances.get(neighborName) && altTransfers < transfers.get(neighborName))) {
                    
                    distances.put(neighborName, altDist);
                    transfers.put(neighborName, altTransfers);
                    previous.put(neighborName, current.station);
                    queue.add(new PathNode(neighborName, altDist, altTransfers));
                }
            }
        }
        
        // 构建路径
        LinkedList<String> path = new LinkedList<>();
        String current = end;
        while (current != null) {
            path.addFirst(current);
            current = previous.get(current);
        }
        
        return new PathResult(path, transfers.get(end), distances.get(end));
    }
    
    private static class PathNode implements Comparable<PathNode> {
        String station;
        double distance;
        int transfers;
        
        public PathNode(String station, double distance, int transfers) {
            this.station = station;
            this.distance = distance;
            this.transfers = transfers;
        }
        
        @Override
        public int compareTo(PathNode other) {
            int distCompare = Double.compare(this.distance, other.distance);
            return distCompare != 0 ? distCompare : Integer.compare(this.transfers, other.transfers);
        }
    }
    
    // 5. 简洁路径展示
    public void printSimplifiedPath(List<String> path) {
        if (path == null || path.size() < 2) {
            System.out.println("无效路径");
            return;
        }
        
        String currentLine = getCommonLine(path.get(0), path.get(1));
        String startStation = path.get(0);
        
        for (int i = 1; i < path.size(); i++) {
            String station = path.get(i);
            String nextLine = (i < path.size() - 1) ? getCommonLine(station, path.get(i + 1)) : currentLine;
            
            if (!nextLine.equals(currentLine)) {
                System.out.printf("乘坐%s从%s到%s，", currentLine, startStation, station);
                startStation = station;
                currentLine = nextLine;
            }
        }
        
        System.out.printf("最后乘坐%s从%s到%s\n", currentLine, startStation, path.get(path.size() - 1));
    }
    
    private String getCommonLine(String station1, String station2) {
        Set<String> lines1 = stations.get(station1).getLines();
        Set<String> lines2 = stations.get(station2).getLines();
        
        for (String line : lines1) {
            if (lines2.contains(line)) {
                return line;
            }
        }
        return "换乘";
    }
    
    // 6-7. 票价计算
    public PriceInfo calculatePrice(List<String> path) {
        double distance = calculatePathDistance(path);
        double singleTicket = calculateSingleTicketPrice(distance);
        double wuhanPass = singleTicket * 0.9;
        double dailyTicket = 0.0;
        
        return new PriceInfo(singleTicket, wuhanPass, dailyTicket);
    }
    
    private double calculatePathDistance(List<String> path) {
        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Station s1 = stations.get(path.get(i));
            Station s2 = stations.get(path.get(i + 1));
            total += s1.getAdjacentStations().get(s2);
        }
        return total;
    }
    
    private double calculateSingleTicketPrice(double distance) {
        if (distance <= 4) return 2.0;
        if (distance <= 8) return 3.0;
        if (distance <= 12) return 4.0;
        if (distance <= 18) return 5.0;
        if (distance <= 24) return 6.0;
        return 7.0;
    }
    
    public Map<String, Station> getStations() {
        return Collections.unmodifiableMap(stations);
    }
    
    public Map<String, Line> getLines() {
        return Collections.unmodifiableMap(lines);
    }
}