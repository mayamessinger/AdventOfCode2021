package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Day2 {
    public static void main(String[] args) {
        var fileName = "resources/day2.txt";
        Position position = new Position();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Command command = new Command(line);
                updatePosition(position, command);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(position.horizontal * position.depth);
    }

    private static void updatePosition(Position position, Command command) {
        switch (command.direction) {
            case forward -> { position.horizontal += command.units; position.depth += (position.aim * command.units); }
            case down -> position.aim += command.units;
            case up -> position.aim -= command.units;
        }
    }

    public static class Position {
        int horizontal;
        int depth;
        int aim;
    }

    public enum Direction {
        forward, down, up
    }

    public static class Command {
        Direction direction;
        int units;

        public Command(String line) {
            var parts = line.split(" ");
            String dir = parts[0];
            int amt = Integer.parseInt(parts[1]);

            direction = dir.equals("forward")
                ? Direction.forward
                : dir.equals("down")
                    ? Direction.down
                    : dir.equals("up")
                        ? Direction.up
                        : null;
            units = amt;
        }
    }
}
