package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Day1 {
    public static void main(String[] args) {
        var fileName = "resources/day1.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int increases = 0;
            String line;

            int windowSize = 3;
            int[] window = new int[windowSize + 1];
            int i = 0;
            while ((line = br.readLine()) != null) {
                int measure = Integer.parseInt(line);

                shiftWindow(window, measure);
                if (i >= windowSize && isIncrease(window))
                    increases += 1;

                i++;
            }

            System.out.println(increases);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void shiftWindow(int[] window, int newMeasure) {
        window[0] = window[1];
        window[1] = window[2];
        window[2] = window[3];
        window[3] = newMeasure;
    }

    private static boolean isIncrease(int[] window) {
        return window[0] + window[1] + window[2] < window[1] + window[2] + window[3];
    }
}