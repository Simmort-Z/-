package com.subway.test;

import com.subway.model.*;
import com.subway.service.SubwaySystem;
import com.subway.util.SubwayFileReader;
import com.subway.util.StationNotFoundException;

import java.io.IOException;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        try {
            SubwaySystem subway = SubwayFileReader.readSubwayData("subway.txt");
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                printMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // 消耗换行符
                
                switch (choice) {
                    case 1:
                        testTransferStations(subway);
                        break;
                    case 2:
                        testNearbyStations(subway, scanner);
                        break;
                    case 3:
                        testAllPaths(subway, scanner);
                        break;
                    case 4:
                        testShortestPath(subway, scanner);
                        break;
                    case 5:
                        testSimplifiedPath(subway, scanner);
                        break;
                    case 6:
                        testPriceCalculation(subway, scanner);
                        break;
                    case 0:
                        System.out.println("退出系统");
                        return;
                    default:
                        System.out.println("无效选择");
                }
            }
        } catch (IOException | StationNotFoundException e) {
            System.err.println("系统错误: " + e.getMessage());
        }
    }
    
    private static void printMenu() {
        System.out.println("\n=== 地铁模拟器系统 ===");
        System.out.println("1. 查看中转站");
        System.out.println("2. 查找附近站点");
        System.out.println("3. 查找所有路径");
        System.out.println("4. 查找最短路径");
        System.out.println("5. 简洁路径展示");
        System.out.println("6. 票价计算");
        System.out.println("0. 退出");
        System.out.print("请选择功能: ");
    }
    
    private static void testTransferStations(SubwaySystem subway) {
        System.out.println("\n=== 中转站列表 ===");
        Map<String, Set<String>> transfers = subway.getTransferStations();
        transfers.forEach((name, lines) -> 
            System.out.println(name + ": " + String.join(", ", lines)));
    }
    
    private static void testNearbyStations(SubwaySystem subway, Scanner scanner) 
            throws StationNotFoundException {
        System.out.print("\n请输入站点名称: ");
        String station = scanner.nextLine().trim();
        System.out.print("请输入查询距离(公里): ");
        double distance = scanner.nextDouble();
        
        Map<String, Map<String, Double>> nearby = subway.findNearbyStations(station, distance);
        System.out.println("\n附近站点:");
        nearby.forEach((name, lineDist) -> 
            lineDist.forEach((line, dist) -> 
                System.out.printf("%s (%s): %.3f公里\n", name, line, dist)));
    }
    
    private static void testAllPaths(SubwaySystem subway, Scanner scanner) 
            throws StationNotFoundException {
        System.out.print("\n请输入起点站: ");
        String start = scanner.nextLine().trim();
        System.out.print("请输入终点站: ");
        String end = scanner.nextLine().trim();
        
        List<List<String>> paths = subway.findAllPaths(start, end);
        System.out.println("\n找到 " + paths.size() + " 条路径:");
        for (int i = 0; i < paths.size(); i++) {
            System.out.println((i + 1) + ": " + String.join(" → ", paths.get(i)));
        }
    }
    
    private static void testShortestPath(SubwaySystem subway, Scanner scanner) 
            throws StationNotFoundException {
        System.out.print("\n请输入起点站: ");
        String start = scanner.nextLine().trim();
        System.out.print("请输入终点站: ");
        String end = scanner.nextLine().trim();
        
        PathResult result = subway.findShortestPath(start, end);
        System.out.println("\n最短路径结果:");
        System.out.println("路径: " + String.join(" → ", result.getPath()));
        System.out.printf("换乘次数: %d, 总距离: %.2f公里\n", 
            result.getTransfers(), result.getDistance());
    }
    
    private static void testSimplifiedPath(SubwaySystem subway, Scanner scanner) 
            throws StationNotFoundException {
        System.out.print("\n请输入起点站: ");
        String start = scanner.nextLine().trim();
        System.out.print("请输入终点站: ");
        String end = scanner.nextLine().trim();
        
        PathResult result = subway.findShortestPath(start, end);
        System.out.println("\n简洁路径:");
        subway.printSimplifiedPath(result.getPath());
    }
    
    private static void testPriceCalculation(SubwaySystem subway, Scanner scanner) 
            throws StationNotFoundException {
        System.out.print("\n请输入起点站: ");
        String start = scanner.nextLine().trim();
        System.out.print("请输入终点站: ");
        String end = scanner.nextLine().trim();
        
        PathResult result = subway.findShortestPath(start, end);
        PriceInfo price = subway.calculatePrice(result.getPath());
        
        System.out.println("\n票价信息:");
        System.out.printf("普通单程票: %.2f元\n", price.getSingleTicket());
        System.out.printf("武汉通(9折): %.2f元\n", price.getWuhanPass());
        System.out.printf("日票: %.2f元\n", price.getDailyTicket());
    }
}