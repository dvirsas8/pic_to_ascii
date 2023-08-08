package image;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class ImageIterableProperty<T> implements Iterable<T> {
    public static final int DEFAULT_PIXEL_NUM = 1;
    private final Image img;
    private final BiFunction<Integer, Integer, T> propertySupplier;
    private final int numOfPixels;

    /**
     * Constructor
     * @param img image to create an iterable for
     * @param propertySupplier a function to iterate with
     * @param numOfPixels number of pixels to iterate at a time
     */
    public ImageIterableProperty(
            Image img,
            BiFunction<Integer, Integer, T> propertySupplier,
            int numOfPixels) {
        this.img = img;
        this.numOfPixels = numOfPixels;
        this.propertySupplier = propertySupplier;
    }

    /**
     /**
     * Constructor
     * @param img image to create an iterable for
     * @param propertySupplier a function to iterate with
     */
    public ImageIterableProperty(
            Image img,
            BiFunction<Integer, Integer, T> propertySupplier) {
        this.img = img;
        this.numOfPixels = DEFAULT_PIXEL_NUM;
        this.propertySupplier = propertySupplier;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int x = 0, y = 0;

            @Override
            public boolean hasNext() {
                return y < img.getHeight();
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                var next = propertySupplier.apply(x, y);
                x += numOfPixels;
                if (x >= img.getWidth()) {
                    x = 0;
                    y += numOfPixels;
                }
                return next;
            }
        };
    }
}
