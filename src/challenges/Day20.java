package challenges;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day20 {
    public static void main(String[] args) {
        ImageAndAlgorithm imageAndAlgorithm = readFile();

        char[][] outputImage = applyEnhancmentAlgo(imageAndAlgorithm.getEnhancementAlgorithm(),
            imageAndAlgorithm.getInputImage(), 50, '.');

        System.out.println(numLitPixels(outputImage));
    }

    private static ImageAndAlgorithm readFile() {
        char[] enhancementAlgorithm = null;
        char[][] inputImage = null;

        String fileName = "resources/day20.txt";

        Path path = Paths.get(fileName);
        try {
            inputImage = new char[(int) Files.lines(path).count() - 2][]; // minus lines for enhancement algo and break
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                if (enhancementAlgorithm == null) {
                    enhancementAlgorithm = line.toCharArray();
                    continue;
                }

                if (line.isEmpty())
                    continue;

                inputImage[row] = line.toCharArray();
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ImageAndAlgorithm(enhancementAlgorithm, inputImage);
    }

    private static char[][] applyEnhancmentAlgo(char[] enhancementAlgo, char[][] inputImage, int times, char paddingChar) {
        if (times == 0)
            return inputImage;

        char[][] outputImage = createSizedEmptyOutputImage(inputImage);
        char[][] paddedInputImage = paddedImage(inputImage, paddingChar);
        for (int i = 0; i < outputImage.length; i++) {
            for (int j = 0; j < outputImage[i].length; j++) {
                int outputPixelLookupIndex = getOutputPixelLookupIndex(i, j, paddedInputImage, paddingChar);

                outputImage[i][j] = enhancementAlgo[outputPixelLookupIndex];
            }
        }

        char nextPaddingChar = paddingChar == '#'
            ? enhancementAlgo[511]
            : enhancementAlgo[0];
        return applyEnhancmentAlgo(enhancementAlgo, outputImage, times - 1, nextPaddingChar);
    }

    private static char[][] createSizedEmptyOutputImage(char[][] inputImage) {
        char[][] outputImage = new char[inputImage.length + 2][inputImage[0].length + 2];

        return outputImage;
    }

    private static int getOutputPixelLookupIndex(int outputX, int outputY, char[][] inputImage, char paddingChar) {
        StringBuilder outputLookupBuilder = new StringBuilder();
        for (int i = outputX - 1; i <= outputX + 1; i++) {
            for (int j = outputY - 1; j <= outputY + 1; j++) {
                if (i < 0 || j < 0 || i >= inputImage.length || j >= inputImage[i].length)
                    outputLookupBuilder.append(paddingChar);
                else
                    outputLookupBuilder.append(inputImage[i][j]);
            }
        }

        String outputLookup = outputLookupBuilder.toString();
        String binaryLookupIndex = outputLookup.replace('.', '0').replace('#', '1');

        return Integer.parseInt(binaryLookupIndex, 2);
    }

    private static char[][] paddedImage(char[][] image, char paddingChar) {
        char[][] paddedImage = new char[image.length + 2][image[0].length + 2];

        paddedImage[0] = new char[image[0].length + 2];
        Arrays.fill(paddedImage[0], paddingChar);
        paddedImage[paddedImage.length - 1] = new char[image[0].length + 2];
        Arrays.fill(paddedImage[paddedImage.length - 1], paddingChar);

        for (int i = 0; i < image.length; i++) {
            paddedImage[i + 1][0] = paddingChar;
            paddedImage[i + 1][paddedImage[i + 1].length - 1] = paddingChar;

            for (int j = 0; j < image[i].length; j++) {
                paddedImage[i + 1][j + 1] = image[i][j];
            }
        }

        return paddedImage;
    }

    private static int numLitPixels(char[][] image) {
        int litPixels = 0;

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (image[i][j] == '#')
                    litPixels++;
            }
        }

        return litPixels;
    }
}

class ImageAndAlgorithm {
    private char[] enhancementAlgorithm;
    char[] getEnhancementAlgorithm() { return enhancementAlgorithm; }

    private char[][] inputImage;
    char[][] getInputImage() { return inputImage; }

    public ImageAndAlgorithm(char[] enhancementAlgorithm, char[][] inputImage) {
        this.enhancementAlgorithm = enhancementAlgorithm;
        this.inputImage = inputImage;
    }
}