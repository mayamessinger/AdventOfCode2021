package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Day4 {
    public static void main(String[] args) {
        var fileName = "resources/day4.txt";

        String[] numbersToCall = new String[] {};
        List<BingoBoard> boards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            List<String> currentBoard = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (numbersToCall.length == 0) {
                    numbersToCall = line.split(",");
                    continue;
                }

                // create boards with values, unmarked
                if (line.equals(""))
                    currentBoard = new ArrayList<>();
                else
                    currentBoard.add(line);

                if (currentBoard.size() == 5) {
                    boards.add(new BingoBoard(currentBoard));
                }
            }

            System.out.println(runBingo(numbersToCall, boards));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int runBingo(String[] valuesToCall, List<BingoBoard> boards) {
        HashSet<BingoBoard> boardsWithBingo = new HashSet<>();

        for (String value : valuesToCall) {
            for (BingoBoard board : boards) {
                board.markSpot(value);

                if (board.hasBingo() && !boardsWithBingo.contains(board) && boardsWithBingo.size() != boards.size() - 1)
                    boardsWithBingo.add(board);
                else if (board.hasBingo() && !boardsWithBingo.contains(board) && boardsWithBingo.size() == boards.size() - 1)
                    return board.getBingoScore(Integer.parseInt(value));
            }
        }

        return -1;
    }
}

class BingoCoordinate {
    private String value;
    public String getValue() { return value; }

    private int x;
    public int getX() { return x; }

    private int y;
    public int getY() { return y; }

    public BingoCoordinate(String value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }
}

class BingoBoard {
    private HashSet<BingoCoordinate> spots;

    private HashSet<BingoCoordinate> markedSpots;

    public void markSpot(String value) {
        markedSpots.addAll(spots.stream().filter(s -> s.getValue().equals(value)).toList());
    }

    public boolean hasBingo() {
        for (int i = 0; i < 5; i++) {
            int iUsable = i;
            if (markedSpots.stream().filter(spot -> spot.getX() == iUsable).count() == 5
                || markedSpots.stream().filter(spot -> spot.getY() == iUsable).count() == 5) {
                return true;
            }
        }

        return false;
    }

    public int getBingoScore(int calledNumber) {
        HashSet<BingoCoordinate> unmarkedSpots = new HashSet<>(spots);
        unmarkedSpots.removeAll(markedSpots);

        int sum = 0;
        for (BingoCoordinate spot:unmarkedSpots) {
            sum += Integer.parseInt(spot.getValue());
        }
        return sum * calledNumber;
    }

    public BingoBoard(List<String> board) {
        spots = new HashSet<>();
        markedSpots = new HashSet<>();

        for (int i = 0; i < board.size(); i++) {
            var rowSplitNormally = board.get(i).trim().replaceAll("\\s+", " ");
            var row = rowSplitNormally.split(" ");

            for (int j = 0; j < row.length; j++) {
                spots.add(new BingoCoordinate(row[j], i, j));
            }
        }
    }
}
