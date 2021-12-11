package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 {
    public static void main(String[] args) {
        List<GarbledEntry> garbledEntries = new ArrayList<>();

        String fileName = "resources/day8.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                garbledEntries.add(parseGarbledEntryFromLine(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(numOccurrencesEasyLetters(garbledEntries));
    }

    private static GarbledEntry parseGarbledEntryFromLine(String line) {
        String[] inputAndOutput = line.split(Pattern.quote(" | "));
        String[] inputNumberStrings = inputAndOutput[0].split(" ");
        String[] outputNumberStrings = inputAndOutput[1].split(" ");

        List<Set<Character>> inputNumbers = new ArrayList<>();
        for (String garbledInput : inputNumberStrings)
            inputNumbers.add(setOfCharacters(garbledInput));

        List<Set<Character>> outputNumbers = new ArrayList<>();
        for (String garbledOutput : outputNumberStrings)
            outputNumbers.add(setOfCharacters(garbledOutput));

        return new GarbledEntry(inputNumbers, outputNumbers);
    }

    private static Set<Character> setOfCharacters(String str) {
        var setOfCharacters = new HashSet<Character>();
        for (char cha : str.toCharArray())
            setOfCharacters.add(cha);

        return setOfCharacters;
    }

    private static int numOccurrencesEasyLetters(List<GarbledEntry> garbledEntries) {
        int totalEasyNumbers = 0;

        for (GarbledEntry entry : garbledEntries) {
            for (Set<Character> outputNumber : entry.getOutputNumbers()) {
                var possibleSsdNumbers = SsdNumbers.numbersWithXSegments(outputNumber.size());
                if (possibleSsdNumbers.contains(SsdNumbers.one) || possibleSsdNumbers.contains(SsdNumbers.four)
                    || possibleSsdNumbers.contains(SsdNumbers.seven) || possibleSsdNumbers.contains(SsdNumbers.eight))
                    totalEasyNumbers++;
            }
        }

        return totalEasyNumbers;
    }

    private static SsdNumber numberFromSegments(Set<Character> segments) {
        List<SsdNumber> matchingNumbers = SsdNumbers.allNumbers.stream().filter(n -> n.getSegments().equals(segments))
            .toList();

        if (matchingNumbers.size() != 1)
            throw new IllegalArgumentException("No SSD number with the provided segments");

        return matchingNumbers.get(0);
    }
}

class GarbledEntry {
    // guaranteed that each possible SSD number will appear exactly once
    private List<Set<Character>> inputNumbers;
    public List<Set<Character>> getInputNumbers() { return inputNumbers; }

    private List<Set<Character>> outputNumbers;
    public List<Set<Character>> getOutputNumbers() { return outputNumbers; }

    public GarbledEntry(List<Set<Character>> inputNumbers, List<Set<Character>> outputNumbers) {
        this.inputNumbers = inputNumbers;
        this.outputNumbers = outputNumbers;
    }
}

class SsdNumber {
    private Set<Character> segments;
    public Set<Character> getSegments() { return segments; }

    private int intValue;
    public int getIntValue() { return intValue; }

    public SsdNumber(Set<Character> segments, int intValue) {
        this.segments = segments;
        this.intValue = intValue;
    }
}

class SsdNumbers {
    public static SsdNumber zero = new SsdNumber(Set.of('a', 'b', 'c', 'e', 'f', 'g'), 0);
    public static SsdNumber one = new SsdNumber(Set.of('c', 'f'), 1);
    public static SsdNumber two = new SsdNumber(Set.of('a', 'c', 'd', 'e', 'g'), 2);
    public static SsdNumber three = new SsdNumber(Set.of('a', 'c', 'd', 'f', 'g'), 3);
    public static SsdNumber four = new SsdNumber(Set.of('b', 'c', 'd', 'f'), 4);
    public static SsdNumber five = new SsdNumber(Set.of('a', 'b', 'd', 'f', 'g'), 5);
    public static SsdNumber six = new SsdNumber(Set.of('a', 'b', 'd', 'e', 'f', 'g'), 6);
    public static SsdNumber seven = new SsdNumber(Set.of('a', 'c', 'f'), 7);
    public static SsdNumber eight = new SsdNumber(Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g'), 8);
    public static SsdNumber nine = new SsdNumber(Set.of('a', 'b', 'c', 'd', 'f', 'g'), 9);

    public static List<SsdNumber> allNumbers = new ArrayList<>(List.of(
        zero, one, two, three, four, five, six, seven, eight, nine
    ));

    public static Set<SsdNumber> numbersWithXSegments(int numSegments) {
        return allNumbers.stream().filter(n -> n.getSegments().size() == numSegments).collect(Collectors.toSet());
    }
}