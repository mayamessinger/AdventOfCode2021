package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day12 {
    public static void main (String[] args) {
        Map<String, Set<String>> caves = readFile();

        System.out.println(getNumPathsForCaves(caves));
    }

    private static Map<String, Set<String>> readFile() {
        Map<String, Set<String>> caves = new HashMap<>();

        String fileName = "resources/day12.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] cavesInLine = line.split("-");
                updateOrAddCaves(caves, cavesInLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return caves;
    }

    private static void updateOrAddCaves(Map<String, Set<String>> caves, String[] newStrings) {
        String cave1 = newStrings[0];
        String cave2 = newStrings[1];

        if (!caves.containsKey(cave1))
            caves.put(cave1, new HashSet<>());
        caves.get(cave1).add(cave2);

        if (!caves.containsKey(cave2))
            caves.put(cave2, new HashSet<>());
        caves.get(cave2).add(cave1);
    }

    private static boolean caveIsSmall(String caveName) {
        return Character.isLowerCase(caveName.charAt(0));
    }

    private static int getNumPathsForCaves(Map<String, Set<String>> caves) {
        return getPathsForCaves(caves).size();
    }

    private static Set<List<String>> getPathsForCaves(Map<String, Set<String>> caves) {
        Set<List<String>> allPaths = pursuePath(caves, new ArrayList<>(Arrays.asList("start")));

        return allPaths.stream().filter(p -> p.contains("end")).collect(Collectors.toSet());
    }

    private static Set<List<String>> pursuePath(Map<String, Set<String>> caves, List<String> path) {
        Set<List<String>> pathsForNode = new HashSet<>();

        for (String connectedCave : caves.get(path.get(path.size() - 1))) {
            List<String> newPath = new ArrayList<>(path);
            newPath.add(connectedCave);
            pathsForNode.add(newPath);

            if (connectedCave.equals("end")) {
                continue;
            }

            if (!path.contains(connectedCave) || !caveIsSmall(connectedCave))
                pathsForNode.addAll(pursuePath(caves, newPath));
        }

        return pathsForNode;
    }
}