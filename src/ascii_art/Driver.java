package ascii_art;

import image.Image;

import java.util.logging.Logger;

/**
 * A class for running the program
 */
public class Driver {
    /**
     * Main method
     * @param args args from the command line
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("USAGE: java asciiArt ");
            return;
        }
        Image img = Image.fromFile(args[0]);
        if (img == null) {
            Logger.getGlobal().severe("Failed to open image file " + args[0]);
            return;
        }
        new Shell(img).run();
    }
}