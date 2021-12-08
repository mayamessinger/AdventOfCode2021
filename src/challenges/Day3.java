package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day3 {
    public static void main(String[] args) {
        String fileName = "resources/day3.txt";

        List<String> allLines = new ArrayList<>() {};
        HashMap<Integer, List<String>> linesWithOneAtKeyIndex = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                allLines.add(line);

                updateOccurrences(linesWithOneAtKeyIndex, line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int oxygenGeneratorRating = calculateRating(linesWithOneAtKeyIndex, allLines, true);
        int co2GeneratorRating = calculateRating(linesWithOneAtKeyIndex, allLines, false);

        System.out.println(oxygenGeneratorRating * co2GeneratorRating);
    }

    private static void updateOccurrences(HashMap<Integer, List<String>> numbersWithOneAtKeyIndex, String line) {
        for (var i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '1') {
                if (numbersWithOneAtKeyIndex.get(i) != null) {
                    numbersWithOneAtKeyIndex.get(i).add(line);
                }
                else {
                    numbersWithOneAtKeyIndex.put(i, new ArrayList<>());
                    numbersWithOneAtKeyIndex.get(i).add(line);
                }
            }
        }
    }

    private static int calculateRating(
        HashMap<Integer, List<String>> linesWithValueAtKeyIndex,
        List<String> allLines,
        boolean retainMostCommon
    ) {
        List<String> filteredLines = new ArrayList<>(allLines);
        for (int i = 0; i < linesWithValueAtKeyIndex.keySet().size(); i++) {
            List<String> numbersWithValueAtI = new ArrayList<>(linesWithValueAtKeyIndex.get(i));
            numbersWithValueAtI.retainAll(filteredLines);
            if (numbersWithValueAtI.size() >= (filteredLines.size() + 1) / 2) {
                if (retainMostCommon)
                    filteredLines.retainAll(numbersWithValueAtI);
                else
                    filteredLines.removeAll(numbersWithValueAtI);
            }
            else {
                if (retainMostCommon)
                    filteredLines.removeAll(numbersWithValueAtI);
                else
                    filteredLines.retainAll(numbersWithValueAtI);
            }

            if (filteredLines.size() == 1)
                break;
        }

        return Integer.parseInt(filteredLines.get(0), 2);
    }
}