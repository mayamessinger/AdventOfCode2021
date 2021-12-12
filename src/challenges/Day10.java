package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Day10 {
    public static void main(String[] args) {
        List<char[]> navigationSubsystem = readFile();

        // System.out.println(calculateSubsystemErrorScore(navigationSubsystem));
        System.out.println(calculateSubsystemAutocompleteScore(navigationSubsystem));
    }

    private static List<char[]> readFile() {
        List<char[]> navigationSubsystem = new ArrayList<>();

        String fileName = "resources/day10.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                navigationSubsystem.add(line.toCharArray());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return navigationSubsystem;
    }

    private static int calculateSubsystemErrorScore(List<char[]>subsystem) {
        int totalScore = 0;

        for (char[] line : subsystem)
            totalScore += calculateLineErrorScore(line);

        return totalScore;
    }

    private static int calculateLineErrorScore(char[] line) {
        return BracketManager.illegalCharacterScore(findErrorIfAny(line));
    }

    private static Character findErrorIfAny(char[] line) {
        Stack<Character> readBrackets = new Stack<>();

        for (char bracket : line) {
            if (BracketManager.isOpeningBracket(bracket))
                readBrackets.push(bracket);
            else {
                if (!BracketManager.isMatchingBracketSet(readBrackets.pop(), bracket))
                    return bracket;
            }
        }

        return null;
    }

    private static long calculateSubsystemAutocompleteScore(List<char[]> subsystem) {
        List<Long> lineScores = calculateLineScores(subsystem);
        Collections.sort(lineScores);

        return lineScores.get(lineScores.size() / 2);
    }

    private static List<Long> calculateLineScores(List<char[]> subsystem) {
        List<Long> lineScores = new ArrayList<>();

        List<char[]> incompleteLines = getIncompleteLines(subsystem);
        for (char[] line : incompleteLines)
            lineScores.add(getAutocompleteScoreForLine(line));

        return lineScores;
    }

    private static List<char[]> getIncompleteLines(List<char[]> lines) {
        List<char[]> incompleteLines = new ArrayList<>();

        for (char[] line : lines) {
            if (findErrorIfAny(line) == null)
                incompleteLines.add(line);
        }

        return incompleteLines;
    }

    private static long getAutocompleteScoreForLine(char[] line) {
        long lineScore = 0;

        List<Character> bracketsToComplete = getBracketsToComplete(line);
        for (char bracket : bracketsToComplete)
            lineScore = BracketManager.adjustLineScore(lineScore, bracket);

        return lineScore;
    }

    private static List<Character> getBracketsToComplete(char[] line) {
        List<Character> bracketsToComplete = new ArrayList<>();

        Stack<Character> readBrackets = new Stack<>();
        for (char bracket : line) {
            if (BracketManager.isOpeningBracket(bracket))
                readBrackets.push(bracket);
            else {
                readBrackets.pop();
            }
        }

        while (!readBrackets.isEmpty()) {
            bracketsToComplete.add(BracketManager.getMatchingClosingBracket(readBrackets.pop()));
        }

        return bracketsToComplete;
    }
}

class BracketManager {
    private static List<Character> openingBrackets = new ArrayList<>(Arrays.asList('(', '[', '{', '<'));

    private static List<Character> closingBrackets = new ArrayList<>(Arrays.asList(')', ']', '}', '>'));

    public static boolean isOpeningBracket(char bracket) {
        return openingBrackets.contains(bracket);
    }

    public static boolean isMatchingBracketSet(char opening, char closing) {
        return closing == getMatchingClosingBracket(opening);
    }

    public static char getMatchingClosingBracket(char opening) {
        switch (opening) {
            case '(':
                return ')';
            case '[':
                return ']';
            case '{':
                return '}';
            case '<':
                return '>';
            default:
                throw new IllegalArgumentException();
        }
    }

    public static int illegalCharacterScore(Character illegal) {
        if (illegal == null)
            return 0;

        switch (illegal) {
            case ')':
                return 3;
            case ']':
                return 57;
            case '}':
                return 1197;
            case '>':
                return 25137;
            default:
                return 0;
        }
    }

    public static long adjustLineScore(long currentScore, char bracket) {
        return currentScore * 5 + getBracketPointValue(bracket);
    }

    private static int getBracketPointValue(char bracket) {
        switch (bracket) {
            case ')':
                return 1;
            case ']':
                return 2;
            case '}':
                return 3;
            case '>':
                return 4;
            default:
                throw new IllegalArgumentException();
        }
    }
}
