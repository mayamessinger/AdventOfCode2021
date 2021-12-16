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
        Set<DijkstraNode> positions = createNodeSet(positionRisks);
        DijkstraNode start = getNodeFromXY(positions, 0, 0);
        DijkstraNode end = getNodeFromXY(positions, positionRisks.length - 1, positionRisks[0].length - 1);

        runDijkstraAlgorithm(positions, start, end);

        return end.getCost();
    }

    private static DijkstraNode getNodeFromXY(Set<DijkstraNode> nodes, int x, int y) {
        List<DijkstraNode> matching = nodes.stream().filter(n -> n.getX() == x && n.getY() == y).toList();

        return matching.size() == 1 ? matching.get(0) : null;
    }

    private static Set<DijkstraNode> createNodeSet(int[][] positionRisks) {
        Set<DijkstraNode> nodes = new HashSet<>();

        for (int i = 0; i < positionRisks.length; i++) {
            for (int j = 0; j < positionRisks[i].length; j++) {
                nodes.add(new DijkstraNode(i, j, positionRisks[i][j]));
            }
        }

        return nodes;
    }

    private static void runDijkstraAlgorithm(Set<DijkstraNode> nodes, DijkstraNode start, DijkstraNode end) {
        Set<DijkstraNode> unvisitedNodes = new HashSet<>(nodes);

        start.setCost(0);
        unvisitedNodes.remove(start);

        DijkstraNode current = start;
        while (unvisitedNodes.contains(end)) {
            Set<DijkstraNode> neighbors = getNeighbors(nodes, current);
            for (DijkstraNode neighbor : neighbors) {
                int potentialCost = current.getCost() + neighbor.getRisk();
                if (neighbor.getCost() > potentialCost)
                    neighbor.setCost(potentialCost);
            }

            unvisitedNodes.remove(current);
            current = bestUnvisitedNode(unvisitedNodes);
        }
    }

    private static Set<DijkstraNode> getNeighbors(Set<DijkstraNode> nodes, DijkstraNode node) {
        Set<DijkstraNode> neighbors = new HashSet<>();

        int[][] possibleNeighborXYs = new int[][] {
            { node.getX() - 1, node.getY() },
            { node.getX() + 1, node.getY() },
            { node.getX(), node.getY() - 1 },
            { node.getX(), node.getY() + 1 }
        };

        for (int[] neighborCoordinate : possibleNeighborXYs) {
            DijkstraNode possibleNeighbor = getNodeFromXY(nodes, neighborCoordinate[0], neighborCoordinate[1]);
            if (possibleNeighbor != null)
                neighbors.add(possibleNeighbor);
        }

        return neighbors;
    }

    private static DijkstraNode bestUnvisitedNode(Set<DijkstraNode> unvisited) {
        return unvisited.stream().min(Comparator.comparingInt(DijkstraNode::getCost)).orElse(null);
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
