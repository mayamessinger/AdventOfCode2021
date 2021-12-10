package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Day6 {
    public static void main(String[] args) {
        String fileName = "resources/day6.txt";
        int[] initialTimers = new int[] {};

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (initialTimers.length == 0)
                    initialTimers = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<Integer, Long> lanternfish = createInitialLanternfish(initialTimers);
        passTime(lanternfish, 256);

        System.out.println(getFishCount(lanternfish));
    }

    private static HashMap<Integer, Long> createInitialLanternfish(int[] initialTimers) {
        HashMap<Integer, Long> lanternfish = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            lanternfish.put(i, (long)0);
        }

        for (int timer : initialTimers) {
            long current = lanternfish.get(timer);
            lanternfish.put(timer, current + 1);
        }

        return lanternfish;
    }

    private static void passTime(HashMap<Integer, Long> lanternfish, int daysToPass) {
        for (int i = 0; i < daysToPass; i++) {
            long newFishForDay = lanternfish.get(0);

            for (int j = 1; j < 9; j++) {
                lanternfish.put(j - 1, lanternfish.get(j));
            }

            lanternfish.put(6, lanternfish.get(6) + newFishForDay); // parents
            lanternfish.put(8, newFishForDay); // babies
        }
    }

    private static long getFishCount(HashMap<Integer, Long> lanternfish) {
        long total = 0;
        for (int i : lanternfish.keySet())
            total += lanternfish.get(i);

        return total;
    }
}