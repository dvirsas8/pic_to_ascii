package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.sql.Timestamp;
import java.util.*;

/**
 * A class for managing the shell and handle user input
 */
public class Shell {
    public static final String DID_NOT_ADD_ERR = "Did not add due to incorrect format";
    public static final String DID_NOT_REMOVE_ERR = "Did not remove due to incorrect format";
    public static final String INCORRECT_COMMAND_ERR = "Did not executed due to incorrect command";
    public static final String EXCEEDING_BOUNDARIES_ERR = "Did not change due to exceeding boundaries";
    public static final String NO_CHARS_ERR = "Did not Execute. There are no characters available.";
    public static final String ALL = "all";
    public static final String SPACE = "space";
    public static final String OUTPUT_PATH = "out.html";
    private static final String FONT = "Courier New";
    public static final int INIT_CHARS_START = 48;
    public static final int INIT_CHARS_END = 57;
    public static final int ALL_CHARS_START = 32;
    public static final int ALL_CHARS_END = 126;
    public static final String ENTER_INPUT = ">>> ";
    public static final String WIDTH_MSG = "Width set to ";
    private final HashSet<Character> charSet = new HashSet<>();
    public static final int RES_FACTOR = 2;
    private final static int MIN_PIXELS_PER_CHAR = 2;
    private final static int INITIAL_CHARS_IN_ROW = 64;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final Image img;

    /**
     * Constructor
     * @param img image to render
     */
    public Shell(Image img) {
        for (char i = INIT_CHARS_START; i <= INIT_CHARS_END; i++) {
            charSet.add(i);
        }
        this.minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        this.img = img;
    }

    /**
     * Runs the program main loop
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        BrightnessImgCharMatcher matcher = new BrightnessImgCharMatcher(img, FONT);
        AsciiOutput output = new HtmlAsciiOutput(OUTPUT_PATH, FONT);
        System.out.print(ENTER_INPUT);
        String userIn = scanner.nextLine();
        while (!Objects.equals(userIn, "exit")) {
            if (userIn.startsWith("add ")) {
                addRemoveIn(userIn, true);
            }
            else if (userIn.startsWith("remove ")) {
                addRemoveIn(userIn, false);
            }
            else if (userIn.equals("chars")) {
                for (char i = ALL_CHARS_START; i < ALL_CHARS_END; i++) {
                    if (charSet.contains(i)) {
                        System.out.print(i + " ");
                    }
                }
                System.out.print("\n");
            }
            else if (userIn.equals("res up") || userIn.equals("res down")) {
                handleRes(userIn);
            }
            else if (userIn.equals("console")) {
                output = new ConsoleAsciiOutput();
            }
            else if (userIn.equals("render")) {
                output = runRender(matcher, output);
            }
            else {
                System.out.println(INCORRECT_COMMAND_ERR);
            }
            System.out.print(ENTER_INPUT);
            userIn = scanner.nextLine();
        }
    }

    private AsciiOutput runRender (BrightnessImgCharMatcher matcher, AsciiOutput output) {
        int size = charSet.size();
        Character[] charArr = new Character[size];
        int j = 0;
        for (char i = ALL_CHARS_START; i <= ALL_CHARS_END; i++) {
            if (charSet.contains(i)) {
                charArr[j] = i;
                j++;
            }
        }
        char[][] charArray = matcher.chooseChars(charsInRow, charArr);
        if (charArray == null) {
            System.out.println(NO_CHARS_ERR);
        }
        else {
            output.output(charArray);
        }
        return output;
    }

    private void handleRes(String userIn) {
        String command = userIn.split(" ")[1];
        if (command.equals("up") && charsInRow * RES_FACTOR <= maxCharsInRow) {
            charsInRow *= RES_FACTOR;
            System.out.println(WIDTH_MSG + charsInRow);
        }
        else if (command.equals("down") && charsInRow / RES_FACTOR >= minCharsInRow) {
            charsInRow /= RES_FACTOR;
            System.out.println(WIDTH_MSG + charsInRow);
        }
        else if ((charsInRow * RES_FACTOR > maxCharsInRow && command.equals("up")) ||
                (charsInRow / RES_FACTOR < minCharsInRow && command.equals("down"))) {
            System.out.println(EXCEEDING_BOUNDARIES_ERR);
        }
    }

    private void addRemoveIn(String userIn, boolean add) {
        String[] splitInput = userIn.split(" ");
        if (splitInput.length != 2) {
            printErr(add);
        }
        else if (splitInput[1].length() == 1) {
            addRemove(splitInput[1].charAt(0), splitInput[1].charAt(0), add);
        }
        else if (splitInput[1].equals(ALL)) {
            addRemove((char) ALL_CHARS_START, (char) ALL_CHARS_END, add);
        }
        else if (splitInput[1].equals(SPACE)) {
            if (add) {
                charSet.add(' ');
            }
            else {
                charSet.remove(' ');
            }
        }
        else if (splitInput[1].length() == 3 && splitInput[1].charAt(1) == '-') {
            String cur = splitInput[1];
            if (cur.charAt(0) >= cur.charAt(2)) {
                addRemove(cur.charAt(2), cur.charAt(0), add);
            }
            else {
                addRemove(cur.charAt(0), cur.charAt(2), add);
            }
        }
        else {
            printErr(add);
        }
    }

    private void printErr(boolean add) {
        if (add) {
            System.out.println(DID_NOT_ADD_ERR);
        }
        else {
            System.out.println(DID_NOT_REMOVE_ERR);
        }
    }

    private void addRemove (char start, char end, boolean add) {
        if (add) {
            for (char i = start; i <= end; i++) {
                charSet.add(i);
            }
        }
        else {
            for (char i = start; i <= end; i++) {
                charSet.remove(i);
            }
        }
    }
}
