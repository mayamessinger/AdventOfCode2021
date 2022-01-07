package challenges;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day21 {
    public static void main(String[] args) {
        int scoreToWin = 1000;

        BoardState boardState = readFile();

        playWithDeterministicDie(boardState, scoreToWin);

        System.out.println(partOneAnswer(boardState, scoreToWin));
    }

    private static BoardState readFile() {
        int p1Pos = 0;
        int p2Pos = 0;

        String fileName = "resources/day21.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = Pattern.compile("starting position: ([0-9]+)").matcher(line);
                matcher.find();

                if (line.contains("Player 1"))
                    p1Pos = Integer.parseInt(matcher.group(1));
                if (line.contains("Player 2"))
                    p2Pos = Integer.parseInt(matcher.group(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new BoardState(p1Pos, p2Pos);
    }

    private static void playWithDeterministicDie(BoardState boardState, int scoreToWin) {
        while (true) {
            DieRoll playerOneRoll = new DieRoll(boardState.getTurnNumber(), 1);
            int p1TurnScore = boardState.movePlayerOne(playerOneRoll);
            boardState.addToPlayerOneScore(p1TurnScore);
            if (winnerId(boardState, scoreToWin) != null)
                break;

            DieRoll playerTwoRoll = new DieRoll(boardState.getTurnNumber(), 2);
            int p2TurnScore = boardState.movePlayerTwo(playerTwoRoll);
            boardState.addToPlayerTwoScore(p2TurnScore);
            if (winnerId(boardState, scoreToWin) != null)
                break;

            boardState.increaseTurnNumber();
        }
    }

    private static Integer winnerId(BoardState boardState, int scoreToWin) {
        if (boardState.getPlayerOneScore() >= scoreToWin)
            return 1;
        if (boardState.getPlayerTwoScore() >= scoreToWin)
            return 2;

        return null;
    }

    private static int partOneAnswer(BoardState boardState, int scoreToWin) {
        int winnerId = winnerId(boardState, scoreToWin);

        int dieRolls = boardState.getTurnNumber() * 6;
        if (winnerId == 1) {
            dieRolls += 3;
            return dieRolls * boardState.getPlayerTwoScore();
        }

        dieRolls += 6;
        return dieRolls * boardState.getPlayerOneScore();
    }
}

class BoardState {
    private int playerOnePosition;
    int movePlayerOne(DieRoll roll) {
        playerOnePosition = move(playerOnePosition, roll.sumOfRolls());
        return playerOnePosition;
    }

    private int playerOneScore;
    int getPlayerOneScore() { return playerOneScore; }
    void addToPlayerOneScore(int points) { this.playerOneScore += points; }

    private int playerTwoPosition;
    int movePlayerTwo(DieRoll roll) {
        playerTwoPosition = move(playerTwoPosition, roll.sumOfRolls());
        return playerTwoPosition;
    }

    private int playerTwoScore;
    int getPlayerTwoScore() { return playerTwoScore; }
    void addToPlayerTwoScore(int points) { this.playerTwoScore += points; }

    private int move(int tokenSpace, int numSpaces) {
        int newPosition = (tokenSpace + numSpaces) % 10;

        return newPosition == 0 ? 10 : newPosition; // handle landing on 10 counting as 10 instead of 0
    }

    private int turnNumber;
    int getTurnNumber() { return turnNumber; }
    void increaseTurnNumber() { turnNumber++; }

    public BoardState(int p1Pos, int p2Pos) {
        this.playerOnePosition = p1Pos;
        this.playerTwoPosition = p2Pos;
        this.playerOneScore = 0;
        this.playerTwoScore = 0;
    }
}

class DieRoll {
    private int rollOne;
    int getRollOne() { return rollOne; }

    private int rollTwo;
    int getRollTwo() { return rollTwo; }

    private int rollThree;
    int getRollThree() { return rollThree; }

    int sumOfRolls() { return rollOne + rollTwo + rollThree; }

    public DieRoll(int turnNumber, int playerToRoll) {
        if (playerToRoll == 1)
            rollOne = (turnNumber * 6 + 1) % 100;
        if (playerToRoll == 2)
            rollOne = (turnNumber * 6 + 4) % 100;

        rollTwo = rollOne + 1;
        rollThree = rollOne + 2;
    }
}