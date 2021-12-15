package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day14 {
    public static void main(String[] args) {
        PolymerProcess polymers = readFile();

        runProcess(polymers, 10);

        System.out.println(mostCommonMinusLeastCommon(polymers.getHead()));
    }

    private static PolymerProcess readFile() {
        LinkedListNode polymerHead = null;
        Set<InsertionRule> rules = new HashSet<>();

        String fileName = "resources/day14.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;

                if (polymerHead == null) {
                    LinkedListNode lastNode = null;

                    char[] polymers = line.toCharArray();
                    for (char polymer : polymers) {
                        LinkedListNode newNode = new LinkedListNode(polymer);

                        if (polymerHead == null)
                            polymerHead = newNode;
                        else
                            lastNode.setNext(newNode);

                        lastNode = newNode;
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

        return new PolymerProcess(polymerHead, rules);
    }

    private static InsertionRule parseInsertionRule(String line) {
        String[] lineSplit = line.split(" -> ");
        char firstElem = lineSplit[0].charAt(0);
        char secondElem = lineSplit[0].charAt(1);
        char toInsert = lineSplit[1].charAt(0);

        return new InsertionRule(firstElem, secondElem, toInsert);
    }

    private static <T> Map<T, Integer> getListOccurrences(LinkedListNode<T> polymerHead) {
        Map<T, Integer> occurrences = new HashMap<>();

        LinkedListNode<T> currentNode = polymerHead;
        while (currentNode != null) {
            T value = currentNode.getValue();
            if (occurrences.get(value) == null)
                occurrences.put(value, 1);
            else
                occurrences.put(value, occurrences.get(value) + 1);

            currentNode = currentNode.getNext();
        }

        return occurrences;
    }

    private static void runProcess(PolymerProcess polymers, int numSteps) {
        for (int i = 0; i < numSteps; i++) {
            LinkedListNode<Character> currentNode = polymers.getHead();
            while (currentNode.hasNext()) {
                LinkedListNode nextNode = currentNode.getNext();
                insertPolymerIfNecessary(polymers.getInsertionRules(), currentNode, nextNode);

                currentNode = nextNode;
            }
        }
    }

    private static void insertPolymerIfNecessary(Set<InsertionRule> insertionRules,
        LinkedListNode<Character> currentNode, LinkedListNode<Character> nextNode
    ) {
        InsertionRule ruleThatApplies = getInsertionRuleForNodes(insertionRules, currentNode.getValue(),
            nextNode.getValue());
        if (ruleThatApplies != null) {
            insertPolymer(currentNode, nextNode, ruleThatApplies.getCharacterToInsert());
        }
    }

    private static <T> void insertPolymer(LinkedListNode<T> firstNode, LinkedListNode<T> secondNode, T newPolymer) {
        LinkedListNode<T> newNode = new LinkedListNode<T>(newPolymer, secondNode);
        firstNode.setNext(newNode);
    }

    private static InsertionRule getInsertionRuleForNodes(Set<InsertionRule> rules, char currentValue, char nextValue) {
        return rules.stream().filter(r -> r.getFirstElement() == currentValue && r.getSecondElement() == nextValue)
            .toList().get(0);
    }

    private static <T> int mostCommonMinusLeastCommon(LinkedListNode<T> head) {
        Map<T, Integer> frequencies = getListOccurrences(head);
        return Collections.max(frequencies.values()) - Collections.min(frequencies.values());
    }

    private static <T> void printAllPolymers(LinkedListNode<T> head) {
        LinkedListNode<T> currentNode = head;
        while (currentNode != null) {
            System.out.print(currentNode.getValue());
            currentNode = currentNode.getNext();
        }
    }
}

class LinkedListNode<T> {
    private T value;
    public T getValue() { return value; }

    private LinkedListNode next;
    public LinkedListNode getNext() { return next; }
    public void setNext(LinkedListNode next) { this.next = next; }
    public boolean hasNext() { return next != null; }

    public LinkedListNode(T value) {
        this.value = value;
        this.next = null;
    }

    public LinkedListNode(T value, LinkedListNode next) {
        this.value = value;
        this.next = next;
    }
}

class PolymerProcess {
    private LinkedListNode head;
    public LinkedListNode getHead() { return head; }

    private Set<InsertionRule> insertionRules;
    public Set<InsertionRule> getInsertionRules() { return insertionRules; }

    public PolymerProcess(LinkedListNode head, Set<InsertionRule> rules) {
        this.head = head;
        this.insertionRules = rules;
    }
}

class InsertionRule {
    private char firstElement;
    public char getFirstElement() { return firstElement; }

    private char secondElement;
    public char getSecondElement() { return secondElement; }

    private char toInsert;
    public char getCharacterToInsert() { return toInsert; }

    public InsertionRule(char firstElem, char secondElem, char toInsert) {
        this.firstElement = firstElem;
        this.secondElement = secondElem;
        this.toInsert = toInsert;
    }
}
