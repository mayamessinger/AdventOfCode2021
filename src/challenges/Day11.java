package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class Day11 {
    public static void main(String[] args) {
        int[][] octopi = readFile();

        // System.out.println(octopiFlashesAfterXDays(octopi, 100));
        System.out.println(dayOfSimultaneousFlash(octopi));
    }

    private static int[][] readFile() {
        int[][] octopi = new int[][] {};

        String fileName = "resources/day11.txt";
        Path path = Paths.get(fileName);
        try {
            octopi = new int[(int) Files.lines(path).count()][];
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                char[] charArray = line.toCharArray();
                octopi[row] = IntStream.range(0, charArray.length).mapToObj(i -> charArray[i])
                    .mapToInt(i -> Integer.parseInt(i.toString())).toArray();

                row++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return octopi;
    }

    private static int octopiFlashesAfterXDays(int[][] octopi, int days) {
        int flashCount = 0;

        for (int i = 0; i < days; i++)
            flashCount += step(octopi);

        return flashCount;
    }

    private static int dayOfSimultaneousFlash(int[][] octopi) {
        int dayOfSimultaneousFlash = -1;

        int stepNumber = 0;
        while (dayOfSimultaneousFlash == -1) {
            stepNumber++;

            int numFlashesInStep = step(octopi);
            if (numFlashesInStep == octopi.length * octopi[0].length)
                dayOfSimultaneousFlash = stepNumber;
        }

        return dayOfSimultaneousFlash;
    }

    private static int step(int[][] octopi) {
        increaseAllEnergy(octopi, 1);
        Set<Coordinate> flashedOctopi = flashEnergeticOctopi(octopi);
        resetFlashes(octopi, flashedOctopi);

        return flashedOctopi.size();
    }

    private static void increaseAllEnergy(int[][] octopi, int amount) {
        for (int i = 0; i < octopi.length; i++) {
            for (int j = 0; j < octopi[i].length; j++) {
                octopi[i][j] += amount;
            }
        }
    }

    private static void resetFlashes(int[][] octopi, Set<Coordinate> octopiToReset) {
        for (Coordinate octopus : octopiToReset)
            octopi[octopus.getX()][octopus.getY()] = 0;
    }

    private static Set<Coordinate> flashEnergeticOctopi(int[][] octopi) {
        Set<Coordinate> flashedOctopi = new HashSet<>();

        while (getFirstOctopusToFlash(octopi, flashedOctopi) != null) {
            Coordinate octopusToFlash = getFirstOctopusToFlash(octopi, flashedOctopi);
            flashedOctopi.add(octopusToFlash);
            increaseEnergyOfSurroundingOctopi(octopi, octopusToFlash);
        }

        return flashedOctopi;
    }

    private static Coordinate getFirstOctopusToFlash(int[][] octopi, Set<Coordinate> flashedOctopi) {
        for (int i = 0; i < octopi.length; i++) {
            for (int j = 0; j < octopi[i].length; j++) {
                if (octopi[i][j] > 9 && !flashedOctopi.contains(new Coordinate(i, j)))
                    return new Coordinate(i, j);
            }
        }

        return null;
    }

    private static void increaseEnergyOfSurroundingOctopi(int[][] octopi, Coordinate flashedOctopus) {
        for (Coordinate octopus : getSurroundingOctopi(octopi, flashedOctopus.getX(), flashedOctopus.getY())) {
            octopi[octopus.getX()][octopus.getY()] += 1;
        }
    }

    private static Set<Coordinate> getSurroundingOctopi(int[][] octopi, int x, int y) {
        Set<Coordinate> surroundingCoordinates = new HashSet<>();

        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i >= 0 && i < octopi.length && j >= 0 && j < octopi[i].length)
                    if (!(i == x && j == y))
                        surroundingCoordinates.add(new Coordinate(i, j));
            }
        }

        return surroundingCoordinates;
    }
}
