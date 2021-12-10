package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        List<Lanternfish> lanternfish = createInitialLanternfish(initialTimers);
        passTime(lanternfish, 80);

        System.out.println(lanternfish.size());
    }

    private static List<Lanternfish> createInitialLanternfish(int[] initialTimers) {
        List<Lanternfish> lanternfish = new ArrayList<>();

        for (int timer : initialTimers) {
            lanternfish.add(new Lanternfish(timer));
        }

        return lanternfish;
    }

    private static void passTime(List<Lanternfish> lanternfish, int daysToPass) {
        for (int i = 0; i < daysToPass; i++) {
            List<Lanternfish> newFishForDay = new ArrayList<>();
            for (Lanternfish fish : lanternfish) {
                if (fish.newDay())
                    newFishForDay.add(new Lanternfish());
            }

            lanternfish.addAll(newFishForDay);
        }
    }
}

class Lanternfish {
    private int timerToOffspring;
    public int getTimerToOffspring() { return timerToOffspring; };

    // returns whether the Lanternfish reproduces on the new day
    public boolean newDay() {
        if (timerToOffspring == 0) {
            timerToOffspring = 6;
            return true;
        }

        timerToOffspring--;
        return false;
    }

    public Lanternfish() {
        timerToOffspring = 8;
    }

    public Lanternfish(int timer) {
        timerToOffspring = timer;
    }
}