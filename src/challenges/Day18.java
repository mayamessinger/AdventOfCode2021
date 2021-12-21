package challenges;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Day18 {
    public static void main(String[] args) {
        List<List<String>> snailfishNumbers = readFile();

        reduceAllNumbers(snailfishNumbers);
        List<String> finalSum = addAllNumbers(snailfishNumbers);
        System.out.println(getMagnitude(finalSum));
    }

    private static List<List<String>> readFile() {
        List<List<String>> snailfishNumbers = new ArrayList<>();

        String fileName = "resources/day18.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> lineToStrings = new ArrayList<>();

                char[] charArray = line.toCharArray();
                for (char i : charArray)
                    lineToStrings.add(String.valueOf(i));

                snailfishNumbers.add(lineToStrings);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return snailfishNumbers;
    }

    private static void reduceAllNumbers(List<List<String>> allNumbers) {
        for (List<String> entireShellfishNumber : allNumbers)
            reduce(entireShellfishNumber);
     }

    private static List<String> addAllNumbers(List<List<String>> allNumbers) {
        List<String> finalNumber = allNumbers.get(0);
        for (int i = 1; i < allNumbers.size(); i++) {
            finalNumber.add(0, "[");
            finalNumber.add(",");
            finalNumber.addAll(allNumbers.get(i));
            finalNumber.add("]");
            reduce(finalNumber);
        }

        return finalNumber;
    }

    private static void reduce(List<String> entireShellfishNumber) {
        while (!isNumberFullyReduced(entireShellfishNumber)) {
            int startIndexOfNumberToExplode = indexNestedInsideFourPairs(entireShellfishNumber);
            if (startIndexOfNumberToExplode != -1) {
                explode(entireShellfishNumber, startIndexOfNumberToExplode);
                continue;
            }

            split(entireShellfishNumber, indexSplittableRegularNumber(entireShellfishNumber));
        }
    }

    private static boolean isNumberFullyReduced(List<String> entireShellfishNumber) {
        return indexNestedInsideFourPairs(entireShellfishNumber) == -1
            && indexSplittableRegularNumber(entireShellfishNumber) == -1;
    }

    // returns the index of the left bracket of the first snailfishNumber in the queue that is nested inside 4 other
    // snailfish numbers. returns -1 if none
    private static int indexNestedInsideFourPairs(List<String> entireShellfishNumber) {
        int leftBracketCounter = 0;

        for (int i = 0; i < entireShellfishNumber.size(); i++) {
            if (entireShellfishNumber.get(i).equals("[")) {
                leftBracketCounter++;

                if (leftBracketCounter == 5)
                    return i;
            }
            if (entireShellfishNumber.get(i).equals("]"))
                leftBracketCounter--;
        }

        return -1;
    }

    private static void explode(List<String> entireShellfishNumber, int indexExplodableNumberStart) {
        int indexExplodableNumberEnd = indexExplodableNumberStart + 4; // always contain regular numbers, known length

        String leftValue = entireShellfishNumber.get(indexExplodableNumberStart + 1);
        int indexToAddLeftNumberTo = firstRegularNumberBetween(entireShellfishNumber, indexExplodableNumberStart, 0);
        if (indexToAddLeftNumberTo != -1) {
            entireShellfishNumber.set(indexToAddLeftNumberTo,
                newStringValueOfInts(entireShellfishNumber.get(indexToAddLeftNumberTo), leftValue));
        }

        String rightValue = entireShellfishNumber.get(indexExplodableNumberEnd - 1);
        int indexToAddRightNumberTo = firstRegularNumberBetween(entireShellfishNumber, indexExplodableNumberEnd, entireShellfishNumber.size() - 1);
        if (indexToAddRightNumberTo != -1)
            entireShellfishNumber.set(indexToAddRightNumberTo, newStringValueOfInts(entireShellfishNumber.get(indexToAddRightNumberTo), rightValue));

        entireShellfishNumber.subList(indexExplodableNumberStart, indexExplodableNumberEnd + 1).clear();
        entireShellfishNumber.add(indexExplodableNumberStart, "0");
    }

    private static int firstRegularNumberBetween(List<String> entireShellfishNumber, int startIndex, int endIndex) {
        if (startIndex > endIndex) {
            for (int i = startIndex; i >= endIndex; i--) {
                if (!Set.of("[", ",", "]").contains(entireShellfishNumber.get(i)))
                    return i;
            }
        }
        else {
            for (int i = startIndex; i <= endIndex; i++) {
                if (!Set.of("[", ",", "]").contains(entireShellfishNumber.get(i)))
                    return i;
            }
        }

        return -1;
    }

    private static String newStringValueOfInts(String stringNumber1, String stringNumber2) {
        return Integer.toString(Integer.parseInt(stringNumber1) + Integer.parseInt(stringNumber2));
    }

    // returns the index of the string of the first snailfishNumber in the queue that has a length of 2+
    // (aka int value of 10+). returns -1 if none
    private static int indexSplittableRegularNumber(List<String> entireShellfishNumber) {
        for (int i = 0; i < entireShellfishNumber.size(); i++) {
            if (entireShellfishNumber.get(i).length() >= 2)
                return i;
        }

        return -1;
    }

    private static void split(List<String> entireShellfishNumber, int indexSplittableRegularNumber) {
        int numberToSplit = Integer.parseInt(entireShellfishNumber.get(indexSplittableRegularNumber));
        List<String> splitNumber = List.of(
            "[",
            Integer.toString(numberToSplit / 2),
            ",",
            Integer.toString((numberToSplit + 1) / 2),
            "]"
            );
        entireShellfishNumber.remove(indexSplittableRegularNumber);
        entireShellfishNumber.addAll(indexSplittableRegularNumber, splitNumber);
    }

    private static int getMagnitude(List<String> finalSum) {
        List<String> magnitudeReduced = new ArrayList<>(finalSum);

        int pairStart = getIndexOfFirstRegularPair(magnitudeReduced);
        while (pairStart != -1) {
            int num1 = Integer.parseInt(magnitudeReduced.get(pairStart + 1));
            int num2 = Integer.parseInt(magnitudeReduced.get(pairStart + 3));

            magnitudeReduced.subList(pairStart, pairStart + 5).clear();
            magnitudeReduced.add(pairStart, Integer.toString(num1 * 3 + num2 * 2));

            pairStart = getIndexOfFirstRegularPair(magnitudeReduced);
        }

        return Integer.parseInt(magnitudeReduced.get(0));
    }

    private static int getIndexOfFirstRegularPair(List<String> number) {
        for (int i = 0; i < number.size() - 4; i++) {
            if (number.get(i).equals("[") && number.get(i + 2).equals(",") && number.get(i + 4).equals("]"))
                return i;
        }

        return -1;
    }
}