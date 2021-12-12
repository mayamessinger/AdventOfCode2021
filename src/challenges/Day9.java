package challenges;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class Day9 {
    public static void main(String[] args) {
        int[][] heightmap = readFile();

        List<Coordinate> lowPoints = getLowPoints(heightmap);
        Map<Coordinate, Integer> basins = getBasins(heightmap, lowPoints);
        int largestBasinSizesMultiplied = getLargestBasinSizesMultiplied(basins);

        // System.out.println(sumRiskLevels(heightmap, lowPoints));
        System.out.println(largestBasinSizesMultiplied);
    }

    private static int[][] readFile() {
        int[][] heightmap = new int[][] {};

        String fileName = "resources/day9.txt";
        Path path = Paths.get(fileName);
        try {
            heightmap = new int[(int)Files.lines(path).count()][];
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                char[] charArray = line.toCharArray();
                heightmap[row] = IntStream.range(0, charArray.length).mapToObj(i -> charArray[i])
                        .mapToInt(i -> Integer.parseInt(i.toString())).toArray();

                row++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return heightmap;
    }

    private static List<Coordinate> getLowPoints(int[][] heightMap) {
        List<Coordinate> lowPoints = new ArrayList<>();

        for (int i = 0; i < heightMap.length; i++) {
            for (int j = 0; j < heightMap[i].length; j++) {
                if (isLowPoint(heightMap, i, j))
                    lowPoints.add(new Coordinate(i, j));
            }
        }

        return lowPoints;
    }

    private static boolean isLowPoint(int[][] heightMap, int x, int y) {
        List<Integer> surroundingValues = getSurroundingValues(heightMap, x, y);

        if (surroundingValues.stream().allMatch(surr -> surr > heightMap[x][y]))
            return true;

        return false;
    }

    private static List<Integer> getSurroundingValues(int[][] heightMap, int x, int y) {
        return getSurroundingCoordinates(heightMap, x, y).stream().map(c -> heightMap[c.getX()][c.getY()]).toList();
    }

    private static Set<Coordinate> getSurroundingCoordinates(int[][] heightMap, int x, int y) {
        Set<Coordinate> surroundingCoordinates = new HashSet<>();

        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i >= 0 && i < heightMap.length && j >= 0 && j < heightMap[i].length)
                    if (i == x ^ j == y)
                        surroundingCoordinates.add(new Coordinate(i, j));
            }
        }

        return surroundingCoordinates;
    }

    private static int sumRiskLevels(int[][] heightMap, List<Coordinate> lowPoints) {
        int sum = 0;
        for (Coordinate point : lowPoints)
            sum += heightMap[point.getX()][point.getY()] + 1;

        return sum;
    }

    private static Map<Coordinate, Integer> getBasins(int[][] heightmap, List<Coordinate> lowPoints) {
        Map<Coordinate, Integer> basins = new HashMap<>();

        for (Coordinate lowPoint : lowPoints) {
            Set<Coordinate> basin = getBasinAroundPoint(heightmap, lowPoint);
            basins.put(lowPoint, basin.size());
        }

        return basins;
    }

    private static Set<Coordinate> getBasinAroundPoint(int[][] heightmap, Coordinate lowPoint) {
        Set<Coordinate> basinPoints = new HashSet<>();

        basinPoints.add(lowPoint);
        Set<Coordinate> surroundingPoints = getSurroundingCoordinates(heightmap, lowPoint.getX(), lowPoint.getY());
        Set<Coordinate> surroundingAndLargerPoints = Set.copyOf(surroundingPoints.stream()
            .filter(c -> pointIsLargerAndNotNine(heightmap, lowPoint, c)).toList());

        for (Coordinate point : surroundingAndLargerPoints) {
            basinPoints.addAll(getBasinAroundPoint(heightmap, point));
        }

        return basinPoints;
    }

    private static boolean pointIsLargerAndNotNine(int[][] heightmap, Coordinate base, Coordinate comparator) {
        int comparatorValue = heightmap[comparator.getX()][comparator.getY()];

        return comparatorValue != 9 && comparatorValue > heightmap[base.getX()][base.getY()];
    }

    private static int getLargestBasinSizesMultiplied(Map<Coordinate, Integer> basins) {
        int multiplyTotal = 1;

        List<Integer> basinSizes = new ArrayList<>(basins.values());
        for (int i = 0; i < 3; i++) {
            int largestRemainingBasin = Collections.max(basinSizes);
            basinSizes.remove(basinSizes.indexOf(largestRemainingBasin));
            multiplyTotal *= largestRemainingBasin;
        }

        return multiplyTotal;
    }
}
