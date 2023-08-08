package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.*;

/**
 * A class that matches characters to subimages according to their brightness
 */
public class BrightnessImgCharMatcher {
    private final static int RENDERER_RESOLUTION = 16;
    public static final int MAX_RGB = 255;
    public static final double RED_PARTIAL = 0.2126;
    public static final double GREEN_PARTIAL = 0.7152;
    public static final double BLUE_PARTIAL = 0.0722;
    private final Image img;
    private final String font;
    private final HashMap<Character, Double> charToDoubleMap = new HashMap<>();
    private TreeMap<Double, Character> doubleToCharMap = new TreeMap<>();
    private final HashMap<Integer, ArrayList<Double>> cache = new HashMap<>();

    /**
     * Constructor
     *
     * @param img  image to process
     * @param font font to use
     */
    public BrightnessImgCharMatcher(Image img, String font) {
        this.img = img;
        this.font = font;
    }

    /**
     * Selects characters to replace subimages according to their brightness
     *
     * @param numCharsInRow number of characters in a row
     * @param charSet       set of characters to choose from
     * @return the output image with all subimages replaced with characters
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        if (charSet.length == 0) {
            return null;
        }
        doubleToCharMap = new TreeMap<>();
        double[] minMaxArray = findMinMax(charSet);
        double minWhiteDist = minMaxArray[0], maxWhiteDist = minMaxArray[1];
        for (var entry : charSet) {
            double curWhiteDist = charToDoubleMap.get(entry);
            double newDist = calculateNewDist(maxWhiteDist, minWhiteDist, curWhiteDist);
            doubleToCharMap.put(newDist, entry);
        }
        int width = img.getWidth();
        int resolution = width / numCharsInRow;
        int numCharsInCol = img.getHeight() / resolution;
        char[][] ret = new char[numCharsInCol][numCharsInRow];
        if (cache.get(resolution) != null) {
            return createImage(resolution, ret, numCharsInRow, true);
        }
        return createImage(resolution, ret, numCharsInRow, false);
    }

    private char[][] createImage(int resolution, char[][] ret, int numCharsInRow,
                                 boolean hashed) {
        int j = 0, i = 0;
        if (!hashed) {
            for (var sub : img.subImages(resolution)) {
                ret[i][j] = findClosestChar(findSubImageGreyscale(sub, resolution));
                j++;
                if (j == numCharsInRow) {
                    i++;
                    j = 0;
                }
            }
        } else {
            ArrayList<Double> arr = cache.get(resolution);
            for (var sub : img.subImages(resolution)) {
                ret[i][j] = findClosestChar(arr.get(i * numCharsInRow + j));
                j++;
                if (j == numCharsInRow) {
                    i++;
                    j = 0;
                }
            }
        }
        return ret;
    }

    private double findSubImageGreyscale(Image image, int resolution) {
        double greyAvg = 0;
        for (Color curPixel : image.pixels()) {
            double greyPixel = curPixel.getRed() * RED_PARTIAL + curPixel.getGreen() * GREEN_PARTIAL +
                    curPixel.getBlue() * BLUE_PARTIAL;
            greyAvg += greyPixel;
        }
        greyAvg /= (image.getHeight() * image.getWidth() * MAX_RGB);
        if (cache.get(resolution) == null) {
            ArrayList<Double> arr = new ArrayList<>();
            arr.add(greyAvg);
            cache.put(resolution, arr);
        } else {
            cache.get(resolution).add(greyAvg);
        }
        return greyAvg;
    }

    private Character findClosestChar(double greyAvg) {
        double closestLarger = 1, closestLower = 0;
        if (doubleToCharMap.ceilingKey(greyAvg) != null && doubleToCharMap.floorKey(greyAvg) != null) {
            closestLarger = doubleToCharMap.ceilingKey(greyAvg);
            closestLower = doubleToCharMap.floorKey(greyAvg);
        } else if (doubleToCharMap.floorKey(greyAvg) != null) {
            return doubleToCharMap.get(doubleToCharMap.floorKey(greyAvg));
        } else if (doubleToCharMap.ceilingKey(greyAvg) != null) {
            return doubleToCharMap.get(doubleToCharMap.ceilingKey(greyAvg));
        }
        if (Math.abs(closestLower - greyAvg) > Math.abs(closestLarger - greyAvg)) {
            return doubleToCharMap.get(closestLarger);
        }
        return doubleToCharMap.get(closestLower);
    }

    private double[] findMinMax(Character[] charSet) {
        double[] ret = new double[2];
        double minWhiteDist = 0;
        double maxWhiteDist = 0;
        for (int i = 0; i < charSet.length; i++) {
            if (i == 0) {
                minWhiteDist = getWhiteCellDist(charSet[i]);
                maxWhiteDist = getWhiteCellDist(charSet[i]);
                charToDoubleMap.put(charSet[i], minWhiteDist);
            } else {
                double whiteDist;
                if (charToDoubleMap.get(charSet[i]) != null) {
                    whiteDist = charToDoubleMap.get(charSet[i]);
                } else {
                    whiteDist = getWhiteCellDist(charSet[i]);
                    charToDoubleMap.put(charSet[i], whiteDist);
                }
                if (whiteDist > maxWhiteDist) {
                    maxWhiteDist = whiteDist;
                } else if (whiteDist < minWhiteDist) {
                    minWhiteDist = whiteDist;
                }
            }
        }
        ret[0] = minWhiteDist;
        ret[1] = maxWhiteDist;
        return ret;
    }

    private double calculateNewDist(double maxWhite, double minWhite, double curWhite) {
        if (minWhite != maxWhite) {
            return (curWhite - minWhite) / (maxWhite - minWhite);
        } else {
            return 0;
        }
    }

    private double getWhiteCellDist(Character ch) {
        int numOfWhiteCells = 0;
        boolean[][] charArray = CharRenderer.getImg(ch, RENDERER_RESOLUTION, font);
        for (int i = 0; i < charArray.length; i++) {
            for (int j = 0; j < charArray[i].length; j++) {
                if (charArray[i][j]) {
                    numOfWhiteCells++;
                }
            }
        }
        return numOfWhiteCells / (double) (charArray.length * charArray[0].length);
    }
}
