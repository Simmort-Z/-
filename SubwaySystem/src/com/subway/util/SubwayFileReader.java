package com.subway.util;

import com.subway.model.Line;
import com.subway.model.Station;
import com.subway.service.SubwaySystem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;  // 添加这行导入
import java.util.regex.Pattern;

public class SubwayFileReader {
    private static final Pattern STATION_PATTERN = 
        Pattern.compile("([^—\\s]+)[—\\-]+([^\\s]+)\\s+([\\d.]+)");
    
    public static SubwaySystem readSubwayData(String filePath) throws IOException {
        SubwaySystem system = new SubwaySystem();
        String currentLineName = null;
        Line currentLine = null;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String lineText;
            while ((lineText = br.readLine()) != null) {
                lineText = lineText.trim();
                
                if (lineText.isEmpty()) {
                    continue;
                }
                
                if (lineText.contains("号线站点间距")) {
                    currentLineName = lineText.split("号线")[0] + "号线";
                    currentLine = new Line(currentLineName);
                    system.addLine(currentLine);
                    continue;
                }
                
                if (lineText.startsWith("站点名称") || lineText.startsWith("间距")) {
                    continue;
                }
                
                var matcher = STATION_PATTERN.matcher(lineText);
                if (matcher.find()) {
                    String station1 = matcher.group(1).trim();
                    String station2 = matcher.group(2).trim();
                    try {
                        double distance = Double.parseDouble(matcher.group(3));
                        
                        system.addStation(station1, currentLineName);
                        system.addStation(station2, currentLineName);
                        system.addConnection(station1, station2, distance, currentLineName);
                        
                        if (currentLine != null) {
                            Map<String, Station> stationMap = system.getStations();
                            Station s1 = stationMap.get(station1);
                            Station s2 = stationMap.get(station2);
                            if (s1 != null) currentLine.addStation(s1);
                            if (s2 != null) currentLine.addStation(s2);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("距离格式错误，跳过此行: " + lineText);
                    }
                } else {
                    System.err.println("无法解析的格式，跳过此行: " + lineText);
                }
            }
        }
        
        return system;
    }
}