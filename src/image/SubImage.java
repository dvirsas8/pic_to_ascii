package image;

import java.awt.*;
import java.util.Objects;

/**
 * A class representing a subimage of a given size
 */
class SubImage implements Image {
    private final int initialX, initialY, resolution;
    private final Image img;

    /**
     * Constructor
     * @param img the image that the subimage is a part of
     * @param initialX starting x-axis point in the image
     * @param initialY starting y-axis point in the image
     * @param resolution resolution of the subimage
     */
    public SubImage(Image img, int initialX, int initialY, int resolution) {
        this.img = img;
        this.initialX = initialX;
        this.initialY = initialY;
        this.resolution = resolution;
    }

    @Override
    public Color getPixel(int x, int y) {
        return img.getPixel(initialX + x, initialY + y);
    }

    @Override
    public int getWidth() {
        return resolution;
    }

    @Override
    public int getHeight() {
        return resolution;
    }
}
