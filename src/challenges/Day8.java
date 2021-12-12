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

        System.out.println(sumOutputNumbers(garbledEntries));
    }

    private static GarbledEntry parseGarbledEntryFromLine(String line) {
        String[] inputAndOutput = line.split(Pattern.quote(" | "));
        String[] inputNumberStrings = inputAndOutput[0].split(" ");
        String[] outputNumberStrings = inputAndOutput[1].split(" ");

        List<Set<Character>> inputNumbers = new ArrayList<>();
        for (String garbledInput : inputNumberStrings)
            inputNumbers.add(setOfCharacters(garbledInput));

        ArrayList<Set<Character>> outputNumbers = new ArrayList<>();
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

    private static int sumOutputNumbers(List<GarbledEntry> garbledEntries) {
        int sum = 0;

        GarbledEntryDecoder decoder = new GarbledEntryDecoder();
        for (GarbledEntry entry : garbledEntries)
            sum += decoder.getOutputNumber(entry);

        return sum;
    }
}

class GarbledEntry {
    // guaranteed that each possible SSD number will appear exactly once
    private List<Set<Character>> inputNumbers;
    public List<Set<Character>> getInputNumbers() { return inputNumbers; }

    private ArrayList<Set<Character>> outputNumbers;
    public ArrayList<Set<Character>> getOutputNumbers() { return outputNumbers; }

    public GarbledEntry(List<Set<Character>> inputNumbers, ArrayList<Set<Character>> outputNumbers) {
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

class GarbledEntryDecoder {
    public static HashMap<SsdNumber, Set<Character>> matchInputSetsToSsdNumber(List<Set<Character>> inputs) {
        HashMap<SsdNumber, Set<Character>> decodedSsdNumbers = new HashMap<>();

        decodedSsdNumbers.put(SsdNumbers.one, setsWithXLength(inputs, 2).get(0));
        decodedSsdNumbers.put(SsdNumbers.seven, setsWithXLength(inputs, 3).get(0));
        decodedSsdNumbers.put(SsdNumbers.four, setsWithXLength(inputs, 4).get(0));
        decodedSsdNumbers.put(SsdNumbers.eight, setsWithXLength(inputs, 7).get(0));
        decodedSsdNumbers.put(SsdNumbers.three, setWithGivenSubsets(inputs, 5,
            new ArrayList<>(Arrays.asList( decodedSsdNumbers.get(SsdNumbers.one),
                decodedSsdNumbers.get(SsdNumbers.seven) )), true));
        decodedSsdNumbers.put(SsdNumbers.zero, setWithGivenSubsets(inputs, 6,
            new ArrayList<>(Arrays.asList( decodedSsdNumbers.get(SsdNumbers.one),
                decodedSsdNumbers.get(SsdNumbers.seven) )), true));
        decodedSsdNumbers.put(SsdNumbers.nine, setWithGivenSubsets(inputs, 6, // nine also contains five but we don't know what it is
            new ArrayList<>(Arrays.asList( decodedSsdNumbers.get(SsdNumbers.one),
                decodedSsdNumbers.get(SsdNumbers.seven), decodedSsdNumbers.get(SsdNumbers.four) )), false));
        decodedSsdNumbers.put(SsdNumbers.five, getFiveLogic(inputs, decodedSsdNumbers));
        decodedSsdNumbers.put(SsdNumbers.six, getSixLogic(inputs, decodedSsdNumbers));
        decodedSsdNumbers.put(SsdNumbers.two, getTwoLogic(inputs, decodedSsdNumbers));

        return decodedSsdNumbers;
    }

    private static List<Set<Character>> setsWithXLength(List<Set<Character>> inputNumbers, int size) {
        List<Set<Character>> matchingNumbers = inputNumbers.stream().filter(n -> n.size() == size)
            .toList();

        return matchingNumbers;
    }

    // finds the single size-length set in inputNumbers that has the given subsets as subsets
    // if exclusive is true, will enforce that the given subsets are the ONLY subsets of the return value
    private static Set<Character> setWithGivenSubsets(List<Set<Character>> inputNumbers, int size,
        List<Set<Character>> subsets, boolean exclusive
    ) {
        List<Set<Character>> sizeFilteredSets = setsWithXLength(inputNumbers, size);

        List<Set<Character>> subsetsToAvoid = new ArrayList<>( inputNumbers );
        subsetsToAvoid.removeAll(subsets);
        return sizeFilteredSets.stream().filter(n ->
                givenSetsAreSubsets(n, subsets) // contains all desired subsets, and does not contain undesired subsets
                && (!exclusive || noSetsAreSubsets(n, subsetsToAvoid))
            ).toList().get(0);
    }

    // 5-letter set that is not equal to SsdNumbers.three's set (known) and is a subset of SsdNumber.nine's set (known)
    private static Set<Character> getFiveLogic(List<Set<Character>> inputNumbers,
        HashMap<SsdNumber, Set<Character>> decodedNumbers
    ) {
        List<Set<Character>> sizeFilteredSets = setsWithXLength(inputNumbers, 5);

        return sizeFilteredSets.stream().filter(n -> !n.equals(decodedNumbers.get(SsdNumbers.three))
            && givenSetsAreSubsets(decodedNumbers.get(SsdNumbers.nine), new ArrayList<>(Arrays.asList(n)))).toList()
            .get(0);
    }

    // 6-letter set that has SsdNumbers.five's set (known) as a subset and is not equal to SsdNumbers.nine's set
    private static Set<Character> getSixLogic(List<Set<Character>> inputNumbers,
        HashMap<SsdNumber, Set<Character>> decodedNumbers
    ) {
        List<Set<Character>> sizeFilteredSets = setsWithXLength(inputNumbers, 6);

        return sizeFilteredSets.stream().filter(n -> !n.equals(decodedNumbers.get(SsdNumbers.nine))
            && givenSetsAreSubsets(n, new ArrayList<>(Arrays.asList(decodedNumbers.get(SsdNumbers.five))))).toList()
            .get(0);
    }

    // 6-letter set that has SsdNumbers.five's set (known) as a subset
    private static Set<Character> getTwoLogic(List<Set<Character>> inputNumbers,
        HashMap<SsdNumber, Set<Character>> decodedNumbers
    ) {
        List<Set<Character>> sizeFilteredSets = setsWithXLength(inputNumbers, 5);

        return sizeFilteredSets.stream().filter(n -> !n.equals(decodedNumbers.get(SsdNumbers.three))
            && !n.equals(decodedNumbers.get(SsdNumbers.five))).toList()
            .get(0);
    }

    private static <T> boolean givenSetsAreSubsets(Set<T> set, List<Set<T>> subsets) {
        for (Set<T> subset : subsets) {
            if (!set.containsAll(subset))
                return false;
        }

        return true;
    }

    private static <T> boolean noSetsAreSubsets(Set<T> set, List<Set<T>> subsets) {
        for (Set<T> subset : subsets) {
            if (!set.equals(subset) && set.containsAll(subset))
                return false;
        }

        return true;
    }

    public static int getOutputNumber(GarbledEntry entry) {
        HashMap<SsdNumber, Set<Character>> entrySsdNumbers = matchInputSetsToSsdNumber(entry.getInputNumbers());

        int decodedNumber = 0;
        for (int i = 0; i < entry.getOutputNumbers().size(); i++) {
            decodedNumber += getSsdNumberFromOutputNumber(entrySsdNumbers, entry.getOutputNumbers().get(i))
                .getIntValue() * ((int)Math.pow(10, 3 - i));
        }

        return decodedNumber;
    }

    private static SsdNumber getSsdNumberFromOutputNumber(HashMap<SsdNumber, Set<Character>> ssdNumbers,
        Set<Character> outputNumber
    ) {
        for (SsdNumber ssdNumber : ssdNumbers.keySet()) {
            if (ssdNumbers.get(ssdNumber).equals(outputNumber)) {
                return ssdNumber;
            }
        }

        throw new IllegalArgumentException();
    }
}