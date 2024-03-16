package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 3;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";
//    private static final ArrayList<String> whiteBorders = new ArrayList<>();
    private static final String[] letterBorder = new String[]{null, "a", "b", "c", "d", "e", "f", "g", "h", null};
    private static final String[] numberBorder = new String[]{null, "1", "2", "3", "4", "5", "6", "7", "8", null};
    private static Random rand = new Random();


    public static void main(String [][] board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        //drawHeaders(out);

        //drawTicTacToeBoard(out);
        drawChessBoard(out, board);

//        out.print(SET_BG_COLOR_DARK_GREY);
//        out.print(SET_TEXT_COLOR_LIGHT_GREY);

//        out.print(RESET_TEXT_COLOR);
//        out.print(RESET_BG_COLOR);
    }

    private static void drawChessBoard(PrintStream out, String[][] board) {
        printBorder(out, letterBorder);
        boolean white = true;
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES + 1; i++) {
            for (int j = 0; j < BOARD_SIZE_IN_SQUARES + 1; j++) {
                if (j == 0) {
                    printGraySquare(out, numberBorder[i + 1]);
                    continue;
                }
                if (white) {
                    printWhiteSquare(out, board[i][j]);
                } else {
                    printRedSquare(out, board[i][j]);
                }
                white = !white;
                if (j == BOARD_SIZE_IN_SQUARES - 1) {
                    white = !white;
                    printGraySquare(out, numberBorder[i + 1]);
                    out.print(RESET_TEXT_COLOR);
                    out.print(RESET_BG_COLOR);
                    out.println();
                }
            }
        }
        printBorder(out, letterBorder);
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }

    private static void printBorder(PrintStream out, String[] list) {
        out.print(SET_TEXT_COLOR_BLACK);
        for (int i = 0; i < list.length; i++) {
            printGraySquare(out, list[i]);
        }
        setBlack(out);
        out.println();
    }

    private static void printWhiteSquare(PrintStream out, String input) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(" ");
        out.print(input != null ? input : " ");
        out.print(" ");
    }

    private static void printRedSquare(PrintStream out, String input) {
        out.print(SET_BG_COLOR_RED);
        out.print(" ");
        out.print(input != null ? input : " ");
        out.print(" ");
    }

    private static void printGraySquare(PrintStream out, String input) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(" ");
        out.print(input != null ? input : " ");
        out.print(" ");
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = { "TIC", "TAC", "TOE" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawTicTacToeBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out);

//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                //drawVerticalLine(out);
//                //setBlack(out);
//            }
        }
    }

    private static void drawRowOfSquares(PrintStream out) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                setWhite(out);

                if (squareRow == SQUARE_SIZE_IN_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPlayer(out, rand.nextBoolean() ? X : O);
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
                }

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    // Draw right line
                    setRed(out);
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
                }

                setBlack(out);
            }

            out.println();
        }
    }

    private static void drawVerticalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setYellow(PrintStream out) {
        out.print(SET_BG_COLOR_YELLOW);
        out.print(SET_TEXT_COLOR_YELLOW);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}