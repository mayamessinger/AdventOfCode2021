package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day15 {
    public static void main (String[] args) {
        int[][] positionRisks = readFile();

        System.out.println(lowestRiskPathCost(positionRisks));
    }

    private static int[][] readFile() {
        int[][] positionRisks = new int[][] {};

        String fileName = "resources/day15.txt";
        Path path = Paths.get(fileName);
        try {
            positionRisks = new int[(int) Files.lines(path).count()][];
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                char[] charArray = line.toCharArray();
                positionRisks[row] = IntStream.range(0, charArray.length).mapToObj(i -> charArray[i])
                        .mapToInt(i -> Integer.parseInt(i.toString())).toArray();

                row++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return positionRisks;
    }

    private static int lowestRiskPathCost(int[][] positionRisks) {
        DijkstraNode[][] positions = createNodeArray(positionRisks);
        positions = getExpandedGrid(positions);
        DijkstraNode start = positions[0][0];
        DijkstraNode end = positions[positions.length - 1][positions[0].length - 1];

        runDijkstraAlgorithm(positions, start, end);

        return end.getCost();
    }

    private static DijkstraNode getNodeFromXY(Set<DijkstraNode> nodes, int x, int y) {
        List<DijkstraNode> matching = nodes.stream().filter(n -> n.getX() == x && n.getY() == y).toList();

        return matching.size() == 1 ? matching.get(0) : null;
    }

    private static DijkstraNode[][] createNodeArray(int[][] positionRisks) {
        DijkstraNode[][] nodes = new DijkstraNode[positionRisks.length][positionRisks[0].length];

        for (int i = 0; i < positionRisks.length; i++) {
            for (int j = 0; j < positionRisks[i].length; j++) {
                nodes[i][j] = new DijkstraNode(i, j, positionRisks[i][j]);
            }
        }

        return nodes;
    }

    private static void runDijkstraAlgorithm(DijkstraNode[][] nodes, DijkstraNode start, DijkstraNode end) {
        Set<DijkstraNode> unvisitedNodes = new HashSet<>();

        start.setCost(0);
        unvisitedNodes.remove(start);

        DijkstraNode current = start;
        while (!end.getIsVisited()) {
            Set<DijkstraNode> neighbors = getNeighbors(nodes, current);
            unvisitedNodes.addAll(neighbors.stream().filter(n -> !n.getIsVisited()).toList());
            for (DijkstraNode neighbor : neighbors) {
                int potentialCost = current.getCost() + neighbor.getRisk();
                if (neighbor.getCost() > potentialCost)
                    neighbor.setCost(potentialCost);
            }

            unvisitedNodes.remove(current);
            current.visit();
            current = bestUnvisitedNode(unvisitedNodes);
        }
    }

    private static Set<DijkstraNode> getNeighbors(DijkstraNode[][] nodes, DijkstraNode node) {
        Set<DijkstraNode> neighbors = new HashSet<>();

        int[][] possibleNeighborXYs = new int[][] {
            { node.getX() - 1, node.getY() },
            { node.getX() + 1, node.getY() },
            { node.getX(), node.getY() - 1 },
            { node.getX(), node.getY() + 1 }
        };

        for (int[] neighborCoordinate : possibleNeighborXYs) {
            try {
                DijkstraNode possibleNeighbor = nodes[neighborCoordinate[0]][neighborCoordinate[1]];
                neighbors.add(possibleNeighbor);
            }
            catch (IndexOutOfBoundsException e) {}
        }

        return neighbors;
    }

    private static DijkstraNode bestUnvisitedNode(Set<DijkstraNode> unvisited) {
        return unvisited.stream().min(Comparator.comparingInt(DijkstraNode::getCost)).orElse(null);
    }

    private static DijkstraNode[][] getExpandedGrid(DijkstraNode[][] grid) {
        DijkstraNode[][] expandedGrid = new DijkstraNode[grid.length * 5][grid[0].length * 5];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                for (int k = 1; k < 5; k++) {
                    expandedGrid[i][j] = grid[i][j];
                    addRight(expandedGrid, i, j, k, grid[i][j]);
                    addRightBelow(expandedGrid, i, j, k, grid[i][j]);
                    addBelow(expandedGrid, i, j, k, grid[i][j]);
                    addBelowRight(expandedGrid, i, j, k, grid[i][j]);
                    addDiagonal(expandedGrid, i, j, k, grid[i][j]);
                }
            }
        }

        return expandedGrid;
    }

    private static void addRight(DijkstraNode[][] expandedGrid, int x, int y, int layer, DijkstraNode node) {
        int dimension = expandedGrid.length / 5;
        int newY = layer * dimension + y;
        int newRisk = (node.getRisk() + layer) > 9
            ? (node.getRisk() + layer) - 9
            : (node.getRisk() + layer);
        expandedGrid[x][newY] = new DijkstraNode(x, newY, newRisk);
    }

    private static void addRightBelow(DijkstraNode[][] expandedGrid, int x, int y, int layer, DijkstraNode node) {
        int dimension = expandedGrid.length / 5;
        for (int i = 1; i < layer; i++) {
            int newX = i * dimension + x;
            int newY = layer * dimension + y;
            int newRisk = (node.getRisk() + layer + i) > 9
                ? (node.getRisk() + layer + i) - 9
                : (node.getRisk() + layer + i);
            expandedGrid[newX][newY] = new DijkstraNode(newX, newY, newRisk);
        }
    }

    private static void addBelow(DijkstraNode[][] expandedGrid, int x, int y, int layer, DijkstraNode node) {
        int dimension = expandedGrid.length / 5;
        int newX = layer * dimension + x;
        int newRisk = (node.getRisk() + layer) > 9
            ? (node.getRisk() + layer) - 9
            : (node.getRisk() + layer);
        expandedGrid[newX][y] = new DijkstraNode(newX, y, newRisk);
    }

    private static void addBelowRight(DijkstraNode[][] expandedGrid, int x, int y, int layer, DijkstraNode node) {
        int dimension = expandedGrid.length / 5;
        for (int i = 1; i < layer; i++) {
            int newX = layer * dimension + x;
            int newY = i * dimension + y;
            int newRisk = (node.getRisk() + layer + i) > 9
                    ? (node.getRisk() + layer + i) - 9
                    : (node.getRisk() + layer + i);
            expandedGrid[newX][newY] = new DijkstraNode(newX, newY, newRisk);
        }
    }

    private static void addDiagonal(DijkstraNode[][] expandedGrid, int x, int y, int layer, DijkstraNode node) {
        int dimension = expandedGrid.length / 5;
        int newX = layer * dimension + x;
        int newY = layer * dimension + y;
        int newRisk = (node.getRisk() + layer * 2) > 9
            ? (node.getRisk() + layer * 2) - 9
            : (node.getRisk() + layer * 2);

        expandedGrid[newX][newY] = new DijkstraNode(newX, newY, newRisk);
    }
}

class DijkstraNode {
    private int xValue;
    public int getX() { return xValue; }

    private int yValue;
    public int getY() { return yValue; }

    private int risk;
    public int getRisk() { return risk; }

    private int costToVisit;
    public int getCost() { return costToVisit; }
    public void setCost(int cost) { costToVisit = cost; }

    private boolean isVisited;
    public boolean getIsVisited() { return isVisited; }
    public void visit() { isVisited = true; }

    public int hashCode() {
        return xValue * 7919 + yValue * 5897;
    }

    public boolean equals(Object other) {
        return other instanceof DijkstraNode
            && xValue == ((DijkstraNode)other).getX() && yValue == ((DijkstraNode)other).getY();
    }

    public DijkstraNode(int x, int y, int risk) {
        this.xValue = x;
        this.yValue = y;
        this.risk = risk;
        this.costToVisit = Integer.MAX_VALUE;
    }
}
