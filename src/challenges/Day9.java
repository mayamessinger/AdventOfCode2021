package challenges;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day9 {
    public static void main(String[] args) {
        int[][] heightmap = readFile();

        List<Integer> lowPoints = getLowPoints(heightmap);
        int totalRiskLevel = sumRiskLevels(lowPoints);

        System.out.println(totalRiskLevel);
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

    private static List<Integer> getLowPoints(int[][] heightMap) {
        List<Integer> lowPoints = new ArrayList<>();

        for (int i = 0; i < heightMap.length; i++) {
            for (int j = 0; j < heightMap[i].length; j++) {
                if (isLowPoint(heightMap, i, j))
                    lowPoints.add(heightMap[i][j]);
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
        List<Integer> surroundingValues = new ArrayList<>();

        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i >= 0 && i < heightMap.length && j >= 0 && j < heightMap[i].length)
                    if (i == x ^ j == y)
                        surroundingValues.add(heightMap[i][j]);
            }
        }

        return surroundingValues;
    }

    private static int sumRiskLevels(List<Integer> lowPoints) {
        int sum = 0;
        for (int point : lowPoints)
            sum += point + 1;

        return sum;
    }
}
