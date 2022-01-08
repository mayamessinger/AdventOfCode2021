package challenges;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22 {
    public static void main(String[] args) {
        List<Command> commands = readFile();

        boolean[][][] initializationSpace = processCommands(commands);

        System.out.println(numOnBlocks(initializationSpace));
    }

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
}

class Command {
    private boolean isTurnOn;
    boolean getIsTurnOn() { return isTurnOn; }

    private int xStart;
    int getXStart() { return xStart; }

    private int xEnd;
    int getXEnd() { return xEnd; }

    private int yStart;
    int getYStart() { return yStart; }

    private int yEnd;
    int getYEnd() { return yEnd; }

    private int zStart;
    int getZStart() { return zStart; }

    private int zEnd;
    int getZEnd() { return zEnd; }

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
        this.isTurnOn = isTurnOn;
        this.xStart = x1;
        this.xEnd = x2;
        this.yStart = y1;
        this.yEnd = y2;
        this.zStart = z1;
        this.zEnd = z2;
    }
}