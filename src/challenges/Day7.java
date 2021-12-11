package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Day7 {
    public static void main(String[] args) {
        String fileName = "resources/day7.txt";
        int[] crabPositions = new int[] {};

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (crabPositions.length == 0) {
                    String[] positionsStrings = line.split(",");
                    crabPositions = Arrays.stream(positionsStrings).mapToInt(Integer::parseInt).toArray();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int optimalPosition = calculateOptimalPositionCost(crabPositions);

        System.out.println(optimalPosition);
    }

    private static int calculateOptimalPositionCost(int[] crabPositions) {
        int positionsMin = Arrays.stream(crabPositions).min().getAsInt();
        int positionsMax = Arrays.stream(crabPositions).max().getAsInt();
        int[][] movementCosts = new int[positionsMax + 1][positionsMax + 1]; // memoization table

        // index represents position, value is total cost for all crabs to move to position
        int[] costsOfPositions = new int[movementCosts.length];
        for (int i = positionsMin; i <= positionsMax; i++) { // calculate cost for every possible (reasonable) position
            for (int crab : crabPositions) {
                costsOfPositions[i] += getMovementCost(movementCosts, crab, i);
            }
        }

        return Arrays.stream(costsOfPositions).min().getAsInt();
    }

    private static int getMovementCost(int[][] movementCosts, int crabPosition, int position) {
        CostCalculator calculator = (pos1, pos2) -> {
            int distance = Math.abs(pos1 - pos2);
            return (distance * distance + distance) / 2;
        };
        if (movementCosts[crabPosition][position] == 0)
            movementCosts[crabPosition][position] = calculator.calculateCost(crabPosition, position);

        return movementCosts[crabPosition][position];
    }
}

interface CostCalculator {
    public int calculateCost(int position1, int position2);
}