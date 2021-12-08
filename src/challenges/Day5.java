package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Day5 {
    public static void main(String[] args) {
        String fileName = "resources/day5.txt";
        GridOfVents grid = new GridOfVents();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Vent vent = parseCoordinates(line);
                tryAddVentToMap(grid, vent);
        }

        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        System.out.println(findOverlapCount(grid, 2));
    }

    private static Vent parseCoordinates(String line) {
        String[] endpoints = line.split(" -> ");
        String[] startCoordinate = endpoints[0].split(",");
        String[] endCoordinate = endpoints[1].split(",");

        return new Vent(new Coordinate(Integer.parseInt(startCoordinate[0]), Integer.parseInt(startCoordinate[1])),
                new Coordinate(Integer.parseInt(endCoordinate[0]), Integer.parseInt(endCoordinate[1])));
    }

    private static void tryAddVentToMap(GridOfVents grid, Vent vent) {
        Set<Coordinate> path = getVentPath(vent);
        path.forEach(p -> grid.addToMap(p));
    }

    private static Set<Coordinate> getVentPath(Vent vent) {
        HashSet<Coordinate> path = new HashSet<>();
        boolean isVertical = vent.getStart().getX() == vent.getEnd().getX();
        boolean isHorizontal = vent.getStart().getY() == vent.getEnd().getY();
        if (isVertical) {
            for (int i : getRange(vent.getStart().getY(), vent.getEnd().getY())) {
                path.add(new Coordinate(vent.getStart().getX(), i));
            }
        }
        else if (isHorizontal) {
            for (int i : getRange(vent.getStart().getX(), vent.getEnd().getX())) {
                path.add(new Coordinate(i, vent.getStart().getY()));
            }
        }

        return path;
    }

    private static Set<Integer> getRange(int start, int end) {
        HashSet<Integer> range = new HashSet<>();

        if (start < end) {
            for (int i = start; i <= end; i++)
                range.add(i);
        }
        else if (end < start) {
            for (int i = end; i <= start; i++)
                range.add(i);
        }

        return range;
    }

    private static int findOverlapCount(GridOfVents grid, int threshold) {
        return (int) grid.getCoordinateCoverage().values().stream().filter(v -> v >= threshold).count();
    }
}

class Vent {
    private Coordinate start;
    public Coordinate getStart() { return start; }

    private Coordinate end;
    public Coordinate getEnd() { return end; }

    public Vent(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }
}

class Coordinate {
    int x;
    public int getX() { return x; }

    int y;
    public int getY() {return y; }

    // initially had the GridOfVents map have a Coordinate key, but with new Coordinate instances it never identified
    // matching values as the same. And defining a way to check object equivalence took super long.
    // Instead, we use prime numbers to try to make any Coordinate object have a unique key, and use that as a key for
    // the map
    public int uniqueHash() {
        return x * 7919 + y * 5897;
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class GridOfVents {
    private HashMap<Integer, Integer> coordinateCoverage;
    public HashMap<Integer, Integer> getCoordinateCoverage() { return coordinateCoverage; }

    public void addToMap(Coordinate coordinate) {
        if (coordinateCoverage.keySet().contains(coordinate.uniqueHash()))
            coordinateCoverage.put(coordinate.uniqueHash(), coordinateCoverage.get(coordinate.uniqueHash()) + 1);
        else
            coordinateCoverage.put(coordinate.uniqueHash(), 1);
    };

    public GridOfVents() {
        coordinateCoverage = new HashMap<>();
    }
}