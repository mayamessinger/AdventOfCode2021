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
            int lastMeasure = Integer.MAX_VALUE;
            while ((line = br.readLine()) != null) {
                int measure = Integer.parseInt(line);
                if (measure > lastMeasure)
                    increases++;

                lastMeasure = measure;
            }

            System.out.println(increases);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}