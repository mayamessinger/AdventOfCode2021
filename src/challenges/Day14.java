package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day14 {
    public static void main(String[] args) {
        PolymerProcess polymers = readFile();

        Map<Character, Long> frequencies = runProcess(polymers, 40);

        System.out.println(mostCommonMinusLeastCommon(frequencies));
    }

    private static PolymerProcess readFile() {
        Map<String, Long> pairFrequencies = new HashMap<>();
        Map<Character, Long> letterFrequencies = new HashMap<>();
        Set<InsertionRule> rules = new HashSet<>();

        String fileName = "resources/day14.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;

                if (pairFrequencies.isEmpty()) {
                    for (int i = 0; i < line.length(); i++) {
                        if (i < line.length() - 1)
                            addOrUpdateFrequencies(pairFrequencies, line.substring(i, i + 2), (long)1);
                        addOrUpdateFrequencies(letterFrequencies, line.charAt(i), (long)1);
                    }

                    continue;
                }

                rules.add(parseInsertionRule(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new PolymerProcess(pairFrequencies, letterFrequencies, rules);
    }

    private static InsertionRule parseInsertionRule(String line) {
        String[] lineSplit = line.split(" -> ");

        return new InsertionRule(lineSplit[0], lineSplit[1].charAt(0));
    }

    private static <T> void addOrUpdateFrequencies(Map<T, Long> frequencies, T keyToUpdate, Long amount) {
        if (frequencies.get(keyToUpdate) == null)
            frequencies.put(keyToUpdate, amount);
        else
            frequencies.put(keyToUpdate, tryGetMapValue(frequencies, keyToUpdate, 0) + amount);
    }

    private static void subtractFrequencies(Map<String, Long> frequencies, String keyToUpdate, Long amount) {
        frequencies.put(keyToUpdate, tryGetMapValue(frequencies, keyToUpdate, 0) - amount);
    }

    private static Map<Character, Long> runProcess(PolymerProcess polymers, int numSteps) {
        Map<String, Long> pairingFrequencies = polymers.getPairingFrequencies();
        Map<Character, Long> letterFrequencies = polymers.getLetterFrequencies();

        for (int i = 0; i < numSteps; i++) {
            Map<String, Long> newPairingFrequencies = new HashMap<>(pairingFrequencies);
            Map<Character, Long> newLetterFrequencies = new HashMap<>(letterFrequencies);

            for (InsertionRule rule : polymers.getInsertionRules()) {
                long rulePairOccurrences = tryGetMapValue(pairingFrequencies, rule.getPolymerPair(), 0);

                addOrUpdateFrequencies(newPairingFrequencies, rule.getFirstNewKey(), rulePairOccurrences);
                addOrUpdateFrequencies(newPairingFrequencies, rule.getSecondNewKey(), rulePairOccurrences);
                addOrUpdateFrequencies(newLetterFrequencies, rule.getCharacterToInsert(), rulePairOccurrences);
                subtractFrequencies(newPairingFrequencies, rule.getPolymerPair(), rulePairOccurrences);
            }

            pairingFrequencies = newPairingFrequencies;
            letterFrequencies = newLetterFrequencies;
        }

        return letterFrequencies;
    }

    private static <T> long tryGetMapValue(Map<T, Long> map, T key, long fallback) {
        Long value = map.get(key);
        if (value == null)
            return fallback;

        return value;
    }

    private static <T> long mostCommonMinusLeastCommon(Map<T, Long> frequencies) {
        return Collections.max(frequencies.values()) - Collections.min(frequencies.values());
    }
}

class PolymerProcess {
    private Map<String, Long> pairingFrequencies;
    public Map<String, Long> getPairingFrequencies() { return pairingFrequencies; }

    private Map<Character, Long> letterFrequencies;
    public Map<Character, Long> getLetterFrequencies() { return letterFrequencies; }

    private Set<InsertionRule> insertionRules;
    public Set<InsertionRule> getInsertionRules() { return insertionRules; }

    public PolymerProcess(Map<String, Long> pairFreqs, Map<Character, Long> letterFreqs, Set<InsertionRule> rules) {
        this.pairingFrequencies = pairFreqs;
        this.letterFrequencies = letterFreqs;
        this.insertionRules = rules;
    }
}

class InsertionRule {
    private String polymerPair;
    public String getPolymerPair() { return polymerPair; }

    public String getFirstNewKey() { return polymerPair.charAt(0) + String.valueOf(toInsert); }

    public String getSecondNewKey() { return String.valueOf(toInsert) + polymerPair.charAt(1); }

    private char toInsert;
    public char getCharacterToInsert() { return toInsert; }

    public InsertionRule(String polymerPair, char toInsert) {
        this.polymerPair = polymerPair;
        this.toInsert = toInsert;
    }
}
