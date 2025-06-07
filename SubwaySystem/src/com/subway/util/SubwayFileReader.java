package com.subway.util;

import com.subway.model.Line;
import com.subway.model.Station;
import com.subway.service.SubwaySystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class SubwayFileReader {
    // 匹配 "站点A---站点B 距离" 或 "站点A—站点B 距离" 格式
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
                
                // 跳过空行
                if (lineText.isEmpty()) {
                    continue;
                }
                
                // 检测线路标题行 (如 "1号线站点间距")
                if (lineText.contains("号线站点间距")) {
                    currentLineName = lineText.split("号线")[0] + "号线";
                    currentLine = new Line(currentLineName);
                    system.addLine(currentLine);
                    continue;
                }
                
                // 跳过表头行 (如 "站点名称 间距（KM）")
                if (lineText.startsWith("站点名称") || lineText.startsWith("间距")) {
                    continue;
                }
                
                // 处理站点数据行
                var matcher = STATION_PATTERN.matcher(lineText);
                if (matcher.find()) {
                    String station1 = matcher.group(1).trim();
                    String station2 = matcher.group(2).trim();
                    try {
                        double distance = Double.parseDouble(matcher.group(3));
                        
                        // 添加到系统
                        system.addStation(station1, currentLineName);
                        system.addStation(station2, currentLineName);
                        system.addConnection(station1, station2, distance, currentLineName);
                        
                        // 添加到当前线路
                        if (currentLine != null) {
                            Station s1 = system.getStations().get(station1);
                            Station s2 = system.getStations().get(station2);
                            if (s1 != null && !currentLine.getStations().contains(s1)) {
                                currentLine.addStation(s1);
                            }
                            if (s2 != null && !currentLine.getStations().contains(s2)) {
                                currentLine.addStation(s2);
                            }
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