package challenges;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22 {
    public static void main(String[] args) {
        List<Command> commands = readFile();

        Set<Range> cubesLeftOn = cubesLeftOn(commands);

        System.out.println(numOnBlocks(cubesLeftOn));
    }

/*
    public static void main(String[] args) {
        List<Command> commands = readFile();

        boolean[][][] initializationSpace = processCommands(commands);

        System.out.println(numOnBlocks(initializationSpace));
    }
 */

    private static List<Command> readFile() {
        List<Command> commands = new ArrayList<>();

        String fileName = "resources/day22.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = Pattern.compile("(on|off) x=([-0-9]+)..([-0-9+]+),y=([-0-9]+)..([-0-9]+),z=([-0-9]+)..([-0-9]+)").matcher(line);
                matcher.find();

                commands.add(new Command(matcher.group(1).equals("on"),
                    Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), // x start, x end
                    Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)), // y start, y end
                    Integer.parseInt(matcher.group(6)), Integer.parseInt(matcher.group(7)))); // z start, z end
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commands;
    }

    private static boolean[][][] processCommands(List<Command> commands) {
        boolean[][][] initializationSpace = new boolean[101][101][101];

        for (Command command : commands) {
            Command sanitizedCommand = command.sanitize();
            if (sanitizedCommand == null)
                continue;

            for (int i = sanitizedCommand.getXStart(); i <= sanitizedCommand.getXEnd(); i++) {
                for (int j = sanitizedCommand.getYStart(); j <= sanitizedCommand.getYEnd(); j++) {
                    for (int k = sanitizedCommand.getZStart(); k <= sanitizedCommand.getZEnd(); k++) {
                        initializationSpace[i + 50][j + 50][k + 50] = command.getIsTurnOn();
                    }
                }
            }
        }

        return initializationSpace;
    }

    private static int numOnBlocks(boolean[][][] initializationSpace) {
        int onBlocks = 0;

        for (int i = 0; i < initializationSpace.length; i++) {
            for (int j = 0; j < initializationSpace[i].length; j++) {
                for (int k = 0; k < initializationSpace[i][j].length; k++) {
                    if (initializationSpace[i][j][k])
                        onBlocks++;
                }
            }
        }

        return onBlocks;
    }

    private static Set<Range> cubesLeftOn(List<Command> commands) {
        Set<Range> onCubes = new HashSet<>();

        for (Command command : commands) {
            if (command.getIsTurnOn()) {
                onCubes.addAll(newOnCubesFromCommand(onCubes, command));
            }
            else {
                for (Range on : new HashSet<>(onCubes))
                {
                    Range intersection = getIntersection(on, command);
                    if (intersection == null)
                        continue;

                    onCubes.remove(on);
                    onCubes.addAll(rangeWithoutIntersection(on, intersection));
                }
            }
        }

        return onCubes;
    }

    private static Set<Range> newOnCubesFromCommand(Set<Range> onCubes, Range newCommand) {
        Set<Range> newOnCubes = new HashSet<>(Set.of(newCommand));

        for (Range onCube : onCubes)
        {
            while (newOnCubes.stream().anyMatch(noc -> getIntersection(noc, onCube) != null))
            {
                Set<Range> newNewOnCubes = new HashSet<>(newOnCubes);

                for (Range newCube : newOnCubes)
                {
                    Range intersection = getIntersection(onCube, newCube);
                    if (intersection == null)
                        continue;

                    newNewOnCubes.remove(newCube);
                    newNewOnCubes.addAll(rangeWithoutIntersection(newCube, intersection));
                }

                newOnCubes = newNewOnCubes;
            }
        }

        return newOnCubes;
    }

    private static Range getIntersection(Range range1, Range range2) {
        int xOverlapStart = (range1.getXStart() <= range2.getXStart() && range1.getXEnd() >= range2.getXStart()) // range1 starts at/to left and goes into/past
            ? range2.getXStart()
            : (range1.getXStart() >= range2.getXStart() && range1.getXStart() <= range2.getXEnd()) // range1 starts within
                ? range1.getXStart()
                : Integer.MIN_VALUE;
        if (xOverlapStart == Integer.MIN_VALUE)
            return null;
        int xOverlapEnd = Math.min(range1.getXEnd(), range2.getXEnd());

        int yOverlapStart = (range1.getYStart() <= range2.getYStart() && range1.getYEnd() >= range2.getYStart()) // range1 starts at/to left and goes into/past
                ? range2.getYStart()
                : (range1.getYStart() >= range2.getYStart() && range1.getYStart() <= range2.getYEnd()) // range1 starts within
                ? range1.getYStart()
                : Integer.MIN_VALUE;
        if (yOverlapStart == Integer.MIN_VALUE)
            return null;
        int yOverlapEnd = Math.min(range1.getYEnd(), range2.getYEnd());

        int zOverlapStart = (range1.getZStart() <= range2.getZStart() && range1.getZEnd() >= range2.getZStart()) // range1 starts at/to left and goes into/past
                ? range2.getZStart()
                : (range1.getZStart() >= range2.getZStart() && range1.getZStart() <= range2.getZEnd()) // range1 starts within
                ? range1.getZStart()
                : Integer.MIN_VALUE;
        if (zOverlapStart == Integer.MIN_VALUE)
            return null;
        int zOverlapEnd = Math.min(range1.getZEnd(), range2.getZEnd());

        return new Range(xOverlapStart, xOverlapEnd, yOverlapStart, yOverlapEnd, zOverlapStart, zOverlapEnd);
    }

    private static Set<Range> rangeWithoutIntersection(Range toAlter, Range toRemove) {
        if (toRemove == null)
            return new HashSet<>(Set.of(toAlter));

        HashSet<Range> remainingRanges = new HashSet<>();

        // to left and also left-top/bottom and left-front/back
        if (toAlter.getXStart() < toRemove.getXStart())
            remainingRanges.add(new Range(toAlter.getXStart(), toRemove.getXStart() - 1,
                toAlter.getYStart(), toAlter.getYEnd(), toAlter.getZStart(), toAlter.getZEnd()));
        // to right and also right-top/bottom and right-front/back
        if (toAlter.getXEnd() > toRemove.getXEnd())
            remainingRanges.add(new Range(toRemove.getXEnd() + 1, toAlter.getXEnd(),
                    toAlter.getYStart(), toAlter.getYEnd(), toAlter.getZStart(), toAlter.getZEnd()));
        // front and also front-above/below
        if (toAlter.getYStart() < toRemove.getYStart())
            remainingRanges.add(new Range(toRemove.getXStart(), toRemove.getXEnd(),
                    toAlter.getYStart(), toRemove.getYStart() - 1, toAlter.getZStart(), toAlter.getZEnd()));
        // back and also back-above/below
        if (toAlter.getYEnd() > toRemove.getYEnd())
            remainingRanges.add(new Range(toRemove.getXStart(), toRemove.getXEnd(),
                    toRemove.getYEnd() + 1, toAlter.getYEnd(), toAlter.getZStart(), toAlter.getZEnd()));
        // above
        if (toAlter.getZStart() < toRemove.getZStart())
            remainingRanges.add(new Range(toRemove.getXStart(), toRemove.getXEnd(),
                    toRemove.getYStart(), toRemove.getYEnd(), toAlter.getZStart(), toRemove.getZStart() - 1));
        // below
        if (toAlter.getZEnd() > toRemove.getZEnd())
            remainingRanges.add(new Range(toRemove.getXStart(), toRemove.getXEnd(),
                    toRemove.getYStart(), toRemove.getYEnd(), toRemove.getZEnd() + 1, toAlter.getZEnd()));

        return remainingRanges;
    }

    private static long numOnBlocks(Set<Range> onBlocks) {
        long numOn = 0;

        for (Range block : onBlocks)
            numOn += block.area();

        return numOn;
    }
}

class Command extends Range {
    private boolean isTurnOn;
    boolean getIsTurnOn() { return isTurnOn; }

    Command sanitize() {
        Command sanitizedCommand = new Command(isTurnOn,
            Math.max(xStart, -50), Math.min(xEnd, 50),
            Math.max(yStart, -50),Math.min(yEnd, 50),
            Math.max(zStart, -50), Math.min(zEnd, 50));

        if (sanitizedCommand.isValid())
            return sanitizedCommand;

        return null;
    }

    boolean isValid() {
        return xStart <= 50 && yStart <= 50 && zStart <= 50;
    }

    public Command(boolean isTurnOn, int x1, int x2, int y1, int y2, int z1, int z2) {
        super(x1, x2, y1, y2, z1, z2);
        this.isTurnOn = isTurnOn;
    }
}

class Range {
    protected int xStart;
    int getXStart() { return xStart; }

    protected int xEnd;
    int getXEnd() { return xEnd; }

    protected int yStart;
    int getYStart() { return yStart; }

    protected int yEnd;
    int getYEnd() { return yEnd; }

    protected int zStart;
    int getZStart() { return zStart; }

    protected int zEnd;
    int getZEnd() { return zEnd; }

    long area() {
        return (long)(this.xEnd - this.xStart + 1)
            * (this.yEnd - this.yStart + 1)
            * (this.zEnd - this.zStart + 1);
    }

    public boolean equals(Object other) {
        return other instanceof Range && xStart == ((Range)other).getXStart() && xEnd == ((Range)other).getXEnd()
            && yStart == ((Range)other).getYStart() && yEnd == ((Range)other).getYEnd()
            && zStart == ((Range)other).getZStart() && zEnd == ((Range)other).getZEnd();
    }

    void copy(Range toCopy) {
        this.xStart = toCopy.getXStart();
        this.xEnd = toCopy.getXEnd();
        this.yStart = toCopy.getYStart();
        this.yEnd = toCopy.getYEnd();
        this.zStart = toCopy.getZStart();
        this.zEnd = toCopy.getZEnd();
    }

    public Range(Range toCopy) {
        this.xStart = toCopy.getXStart();
        this.xEnd = toCopy.getXEnd();
        this.yStart = toCopy.getYStart();
        this.yEnd = toCopy.getYEnd();
        this.zStart = toCopy.getZStart();
        this.zEnd = toCopy.getZEnd();
    }

    public Range(int x1, int x2, int y1, int y2, int z1, int z2) {
        this.xStart = x1;
        this.xEnd = x2;
        this.yStart = y1;
        this.yEnd = y2;
        this.zStart = z1;
        this.zEnd = z2;
    }
}