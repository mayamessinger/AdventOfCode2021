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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day17 {
    public static void main(String[] args) {
        TargetArea targetArea = readFile();

        Map<Integer, Integer> maxHeights = runStepsUntilIntersect(targetArea);
        System.out.println(Collections.max(maxHeights.values()));
    }

    private static TargetArea readFile() {
        int x1 = 0;
        int x2 = 0;
        int y1 = 0;
        int y2 = 0;

        String fileName = "resources/day17.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Pattern xPattern = Pattern.compile("x=([-0-9]+)..([-0-9+]+)");
                Pattern yPattern = Pattern.compile("y=([-0-9]+)..([-0-9+]+)");

                Matcher xMatcher = xPattern.matcher(line);
                xMatcher.find();
                x1 = Integer.parseInt(xMatcher.group(1));
                x2 = Integer.parseInt(xMatcher.group(2));

                Matcher yMatcher = yPattern.matcher(line);
                yMatcher.find();
                y1 = Integer.parseInt(yMatcher.group(1));
                y2 = Integer.parseInt(yMatcher.group(2));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new TargetArea(x1, x2, y1, y2);
    }

    private static Map<Integer, Integer> runStepsUntilIntersect(TargetArea targetArea) {
        Map<Integer, Integer> trajectoryMaxHeights = new HashMap<>();

        for (int i : IntStream.rangeClosed(Math.min(0, targetArea.getMostExtremeX()), Math.max(0, targetArea.getMostExtremeX())).toArray()) {
            for (int j : IntStream.rangeClosed(-150, 150).toArray()) { // TODO: more sophisticated
                Coordinate currentLocation = new Coordinate(0, 0);
                Set<Coordinate> stepPlacements = new HashSet<>();
                Trajectory trajectory = new Trajectory(i, j);
                Coordinate nextLocation = getNextLocation(currentLocation, trajectory);

                while (shouldPursueProbePath(nextLocation, targetArea)) {
                    currentLocation = nextLocation;
                    stepPlacements.add(currentLocation);
                    trajectory.applyDragAndGravity();
                    nextLocation = getNextLocation(currentLocation, trajectory);
                }

                if (intersectsTargetArea(stepPlacements, targetArea)) {
                    // note the initial velocity and the max height produced by it
                    trajectoryMaxHeights.put(i * 7919 + j * 5897,
                        Collections.max(stepPlacements.stream().map(sp -> sp.getY()).toList()));
                }
            }
        }

        return trajectoryMaxHeights;
    }

    private static Coordinate getNextLocation(Coordinate currentLocation, Trajectory trajectory) {
        return new Coordinate(currentLocation.getX() + trajectory.getXVelocity(),
            currentLocation.getY() + trajectory.getYVelocity());
    }

    // returns whether the probe has passed the target area's limits
    private static boolean shouldPursueProbePath(Coordinate nextLocation, TargetArea targetArea) {
        int minAllowableX = Math.min(0, Collections.min(targetArea.getXRange()));
        int maxAllowableX = Math.max(0, Collections.max(targetArea.getXRange()));
        int minAllowableY = Math.min(0, Collections.min(targetArea.getYRange()));

        return minAllowableX <= nextLocation.getX() && maxAllowableX >= nextLocation.getX()
            && minAllowableY <= nextLocation.getY();
    }

    private static boolean intersectsTargetArea(Set<Coordinate> visitedCoordinates, TargetArea targetArea) {
        return visitedCoordinates.stream().anyMatch(c -> targetArea.getXRange().contains(c.getX())
            && targetArea.getYRange().contains(c.getY()));
    }
}

class TargetArea {
    private Set<Integer> xRange;
    public Set<Integer> getXRange() { return xRange; }
    public int getMostExtremeX() {
        return Collections.min(xRange) > 0 ? Collections.max(xRange) : Collections.min(xRange);
    }

    private Set<Integer> yRange;
    public Set<Integer> getYRange() { return yRange; }
    public int getMostExtremeY() {
        return Collections.min(yRange) > 0 ? Collections.max(yRange) : Collections.min(yRange);
    }

    public TargetArea(int x1, int x2, int y1, int y2) {
        xRange = new HashSet<>();
        for (int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++)
            xRange.add(i);

        yRange = new HashSet<>();
        for (int i = Math.min(y1, y2); i <= Math.max(y1, y2); i++)
            yRange.add(i);
    }
}

class Trajectory {
    private int xVelocity;
    public int getXVelocity() { return xVelocity; }

    private int yVelocity;
    public int getYVelocity() { return yVelocity; }

    public void applyDragAndGravity() {
        if (xVelocity > 0)
            xVelocity--;
        else if (xVelocity < 0)
            xVelocity++;

        yVelocity--;
    }

    public Trajectory(int x, int y) {
        xVelocity = x;
        yVelocity = y;
    }
}