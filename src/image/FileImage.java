package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private final Color[][] pixelArray;

    /**
     * Constructor
     * @param filename file to load
     * @throws IOException
     */
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        int widthPower = (int) Math.ceil(Math.log(origWidth) / Math.log(2));
        int heightPower = (int) Math.ceil(Math.log(origHeight) / Math.log(2));
        int newWidth = (int) Math.pow(2, widthPower), newHeight = (int) Math.pow(2, heightPower);
        int heightGap = (newHeight - origHeight) / 2, widthGap = (newWidth - origWidth) / 2;

        pixelArray = new Color[newHeight][newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                if (i < heightGap || i >= heightGap + origHeight || j < widthGap || j >= widthGap + origWidth) {
                    pixelArray[i][j] = DEFAULT_COLOR;
                }
                else {
                    int col = im.getRGB(j - widthGap, i - heightGap);
                    pixelArray[i][j] = new Color(col, true);
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return pixelArray[0].length;
    }

    @Override
    public int getHeight() {
        return pixelArray.length;
    }

    @Override
    public Color getPixel(int x, int y) {
        return pixelArray[y][x];
    }

}
