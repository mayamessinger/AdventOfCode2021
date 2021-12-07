package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Day3 {
    public static void main(String[] args) {
        String fileName = "resources/day3.txt";
        int[] oneOccurrences = null;
        int totalLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (oneOccurrences == null)
                    oneOccurrences = new int[line.length()];

                updateOccurrences(oneOccurrences, line);

                totalLines++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Rates rates = calculateGammaRate(oneOccurrences, totalLines);

        System.out.println(rates.gammaRate * rates.epsilonRate);
    }

    private static void updateOccurrences(int[] occurrences, String line) {
        for (var i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '1')
                occurrences[i] += 1;
        }
    }

    private static Rates calculateGammaRate(int[] occurrences, int totalLines) {
        String gammaRate = "";
        String epsilonRate = "";

        for (int i = 0; i < occurrences.length; i++) {
            if (occurrences[i] > totalLines / 2) {
                gammaRate += "1";
                epsilonRate += "0";
            }
            else {
                gammaRate += "0";
                epsilonRate += "1";
            }
        }

        return new Rates(Integer.parseInt(gammaRate, 2), Integer.parseInt(epsilonRate, 2));
    }

    public static class Rates {
        int gammaRate;
        int epsilonRate;

        public Rates(int gammaRate, int epsilonRate) {
            this.gammaRate = gammaRate;
            this.epsilonRate = epsilonRate;
        }
    }
}