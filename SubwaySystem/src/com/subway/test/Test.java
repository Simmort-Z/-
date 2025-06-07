package com.subway.test;

import com.subway.service.SubwaySystem;
import com.subway.util.SubwayFileReader;
import com.subway.util.StationNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        try {
            // 1. 读取地铁数据
            System.out.println("正在读取地铁数据...");
            SubwaySystem subway = SubwayFileReader.readSubwayData("subway.txt");
            System.out.println("地铁数据读取完成！\n");
            
            // 2. 测试中转站识别
            testTransferStations(subway);
            
            // 3. 测试附近站点查询
            testNearbyStations(subway);
            
            // 4. 测试路径查找
            testPathFinding(subway);
            
        } catch (IOException e) {
            System.err.println("读取文件出错: " + e.getMessage());
        } catch (StationNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void testTransferStations(SubwaySystem subway) {
        System.out.println("=== 中转站识别测试 ===");
        Map<String, Set<String>> transferStations = subway.findTransferStations();
        
        System.out.println("共找到 " + transferStations.size() + " 个中转站：");
        transferStations.forEach((name, lines) -> 
            System.out.println(" - " + name + ": " + String.join(", ", lines)));
        System.out.println();
    }

    private static void testNearbyStations(SubwaySystem subway) throws StationNotFoundException {
        System.out.println("=== 附近站点查询测试 ===");
        String queryStation = "华中科技大学站";
        double distance = 1.0;
        
        System.out.println("查询站点: " + queryStation + ", 距离: " + distance + "公里");
        
        Map<String, Map<String, Double>> nearby = subway.findNearbyStations(queryStation, distance);
        
        if (nearby.isEmpty()) {
            System.out.println("没有找到符合条件的附近站点");
        } else {
            System.out.println("找到 " + nearby.size() + " 个附近站点：");
            nearby.forEach((name, lineDistances) -> {
                lineDistances.forEach((line, dist) -> {
                    System.out.printf(" - %s (%s): %.3f公里\n", name, line, dist);
                });
            });
        }
        System.out.println();
    }

    private static void testPathFinding(SubwaySystem subway) throws StationNotFoundException {
        System.out.println("=== 路径查找测试 ===");
        String start = "华中科技大学站";
        String end = "光谷广场站";
        
        System.out.println("查找从 " + start + " 到 " + end + " 的所有路径");
        
        List<List<String>> paths = subway.findAllPaths(start, end);
        
        if (paths.isEmpty()) {
            System.out.println("没有找到从 " + start + " 到 " + end + " 的路径");
        } else {
            System.out.println("共找到 " + paths.size() + " 条路径：");
            for (int i = 0; i < paths.size(); i++) {
                System.out.println("路径 " + (i+1) + ": " + String.join(" → ", paths.get(i)));
            }
        }
        System.out.println();
    }
}