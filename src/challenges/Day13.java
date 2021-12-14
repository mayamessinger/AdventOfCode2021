package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day13 {
    public static void main(String[] args) {
        PageOne page = readFile();

        // System.out.println(getNumberDotsAfterOneFold(page));
        displayPaperAfterAllFolds(page);
    }

    private static PageOne readFile() {
        Set<Coordinate> dots = new HashSet<>();
        List<Fold> folds = new ArrayList<>();

        String fileName = "resources/day13.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean doneWithDots = false;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    doneWithDots = true;
                    continue;
                }

                if (!doneWithDots)
                    dots.add(parseCoordinate(line));
                else {
                    int indexOfEquals = line.indexOf('=');
                    char axis = line.charAt(indexOfEquals - 1);
                    int value = Integer.parseInt(line.substring(indexOfEquals + 1));

                    folds.add(new Fold(value, axis));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new PageOne(dots, folds);
    }

    private static Coordinate parseCoordinate(String line) {
        String[] split = line.split(",");
        return new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private static int getNumberDotsAfterOneFold(PageOne page) {
        doFold(page.getDots(), page.getFolds().get(1));

        return page.getDots().size();
    }

    private static void displayPaperAfterAllFolds(PageOne page) {
        for (Fold fold : page.getFolds())
            doFold(page.getDots(), fold);

        printPaperAsArray(page.getDots());
    }

    // x and y are reversed for display because the problem switched the normal axes but my brain can't handle that
    private static void printPaperAsArray(Set<Coordinate> dots) {
        int finalXSize = getMaxX(dots);
        int finalYSize = getMaxY(dots);
        char[][] arrayToPrint = new char[finalYSize][finalXSize];
        for (char[] row : arrayToPrint)
            Arrays.fill(row, '.');

        for (Coordinate dot : dots) {
            arrayToPrint[dot.getY()][dot.getX()] = '#';
        }

        for (char[] line : arrayToPrint) {
            System.out.println(line);
        }
    }

    private static int getMaxX(Set<Coordinate> dots) {
        return Collections.max(dots.stream().map(d -> d.getX()).toList()) + 1;
    }

    private static int getMaxY(Set<Coordinate> dots) {
        return Collections.max(dots.stream().map(d -> d.getY()).toList()) + 1;
    }

    private static void doFold(Set<Coordinate> dots, Fold fold) {
        Set<Coordinate> dotsToTranspose = dotsToTranspose(dots, fold);
        dots.removeAll(dotsToTranspose);
        if (fold.axis == 'y') {
            for (Coordinate dot : dotsToTranspose) {
                dots.add(new Coordinate(dot.getX(), fold.getValue() - (dot.getY() - fold.getValue())));
            }
        }
        else if (fold.axis == 'x') {
            for (Coordinate dot : dotsToTranspose) {
                dots.add(new Coordinate(fold.getValue() - (dot.getX() - fold.getValue()), dot.getY()));
            }
        }
    }

    private static Set<Coordinate> dotsToTranspose(Set<Coordinate> dots, Fold fold) {
        Set<Coordinate> dotsToTranspose = new HashSet<>();

        if (fold.getAxis() == 'x') {
            dotsToTranspose.addAll(dots.stream().filter(d -> d.getX() > fold.getValue()).toList());
        }
        else if (fold.getAxis() == 'y') {
            dotsToTranspose.addAll(dots.stream().filter(d -> d.getY() > fold.getValue()).toList());
        }

        return dotsToTranspose;
    }
}

class PageOne {
    private Set<Coordinate> dots;
    public Set<Coordinate> getDots() { return dots; }

    private List<Fold> folds;
    public List<Fold> getFolds() { return folds; }

    public PageOne(Set<Coordinate> dots, List<Fold> folds) {
        this.dots = dots;
        this.folds = folds;
    }
}

class Fold {
    int value;
    public int getValue() { return value; }

    char axis;
    public char getAxis() { return axis; }

    public Fold(int value, char axis) {
        this.value = value;
        this.axis = axis;
    }
}