package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Day10 {
    public static void main(String[] args) {
        List<char[]> navigationSubsystem = readFile();

        System.out.println(calculateSubsystemErrorScore(navigationSubsystem));
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
}

class BracketManager {
    private static List<Character> openingBrackets = new ArrayList<>(Arrays.asList('(', '[', '{', '<'));

    private static List<Character> closingBrackets = new ArrayList<>(Arrays.asList(')', ']', '}', '>'));

    public static boolean isOpeningBracket(char bracket) {
        return openingBrackets.contains(bracket);
    }

    public static boolean isMatchingBracketSet(char opening, char closing) {
        return (opening == '(' && closing == ')')
            || (opening == '[' && closing == ']')
            || (opening == '{' && closing == '}')
            || (opening == '<' && closing == '>');
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
}
