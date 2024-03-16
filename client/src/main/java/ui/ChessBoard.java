package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final String[] letterBorder = new String[]{null, "a", "b", "c", "d", "e", "f", "g", "h", null};
    private static final String[] reverseLetterBorder = new String[]{null, "h", "g", "f", "e", "d", "c", "b", "a", null};
    private static final String[] numberBorder = new String[]{null, "1", "2", "3", "4", "5", "6", "7", "8", null};


    public static void main(String [][] whitePieces, String[][] blackPieces) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        out.print(SET_TEXT_BOLD);

        drawChessBoard(out, whitePieces, blackPieces);
    }

    private static void drawChessBoard(PrintStream out, String[][] whitePieces, String[][] blackPieces) {
        drawBlackSide(out, whitePieces, blackPieces);
        drawWhiteSide(out, whitePieces, blackPieces);
    }

    private static void drawWhiteSide(PrintStream out, String[][] whitePieces, String[][] blackPieces) {
        printBorder(out, letterBorder);
        boolean white = true;
        for (int i = BOARD_SIZE_IN_SQUARES - 1; i >= 0; i--) {
            for (int j = BOARD_SIZE_IN_SQUARES - 1; j >= 0; j--) {
                if (j == BOARD_SIZE_IN_SQUARES - 1) {
                    printGraySquare(out, numberBorder[i + 1]);
                }
                printBoardSquare(out, whitePieces[i][j], blackPieces[i][j], white);
                white = !white;
                if (j == 0) {
                    white = !white;
                    printGraySquare(out, numberBorder[i + 1]);
                    resetAll(out);
                    out.println();
                }
            }
        }
        printBorder(out, letterBorder);
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }

    private static void drawBlackSide(PrintStream out, String[][] whitePieces, String[][] blackPieces) {
        printBorder(out, reverseLetterBorder);
        boolean white = true;
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; i++) {
            for (int j = 0; j < BOARD_SIZE_IN_SQUARES; j++) {
                if (j == 0) {
                    printGraySquare(out, numberBorder[i + 1]);
                }
                printBoardSquare(out, whitePieces[i][j], blackPieces[i][j], white);
                white = !white;
                if (j == BOARD_SIZE_IN_SQUARES - 1) {
                    white = !white;
                    printGraySquare(out, numberBorder[i + 1]);
                    resetAll(out);
                    out.println();
                }
            }
        }
        printBorder(out, reverseLetterBorder);
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }

    private static void resetAll(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void printBorder(PrintStream out, String[] list) {
        out.print(SET_TEXT_COLOR_BLACK);
        for (int i = 0; i < list.length; i++) {
            printGraySquare(out, list[i]);
        }
        setBlack(out);
        out.println();
    }

    private static void printBoardSquare(PrintStream out, String white, String black, boolean isWhite) {
        if (isWhite) {
            out.print(SET_BG_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_RED);
        }
        out.print(" ");
        if (white != null) {
            out.print(SET_TEXT_COLOR_BLUE);
            out.print(white);
        } else if (black != null) {
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(black);
        } else {
            out.print(" ");
        }
        out.print(" ");
        out.print(RESET_TEXT_COLOR);
    }

    private static void printGraySquare(PrintStream out, String input) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(" ");
        out.print(input != null ? input : " ");
        out.print(" ");
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}