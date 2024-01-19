package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessGame.TeamColor pieceColor;
    public PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> chessMoveArrayList = new ArrayList<>();
        return switch (this.getPieceType()) {
            case KING -> kingMoves(board, myPosition, chessMoveArrayList);
            case QUEEN -> queenMoves(board, myPosition, chessMoveArrayList);
            case BISHOP -> bishopMoves(board, myPosition, chessMoveArrayList);
            case KNIGHT -> knightMoves(board, myPosition, chessMoveArrayList);
            case ROOK -> rookMoves(board, myPosition, chessMoveArrayList);
            case PAWN -> pawnMoves(board, myPosition, chessMoveArrayList);
        };
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> chessMoveArrayList) {
        return null;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> chessMoveArrayList) {
        return null;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> chessMoveArrayList) {
        return checkDiagonal(board, myPosition, 8, chessMoveArrayList, null);
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> chessMoveArrayList) {
        return checkStraight(board, myPosition, 8, chessMoveArrayList, null);
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> chessMoveArrayList) {
        return null;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> chessMoveArrayList) {
        return null;
    }

    private Collection<ChessMove> checkDiagonal(ChessBoard board, ChessPosition myPosition, int range, ArrayList<ChessMove> chessMoveArrayList, ChessPiece.PieceType promotionPiece) {
        int row = myPosition.row ;
        int col = myPosition.col;
        // Check north-east
        boolean isCapture = false;
        for (int i = 1; i < range + 1; i++) {
            int newRow = row + i;
            int newCol = col + i;
            if (newRow < board.boardDim && newCol < board.boardDim) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(newRow, newCol));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), promotionPiece));
            }
        }

        // North-west
        isCapture = false;
        for (int i = 1; i < range + 1; i++) {
            int newRow = row + i;
            int newCol = col - i;
            if (newRow < board.boardDim && newCol > 0) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(newRow, newCol));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), promotionPiece));
            }
        }

        // South-east
        isCapture = false;
        for (int j = 1; j < range + 1; j++) {
            int newRow = row - j;
            int newCol = col + j;
            if (newRow > 0 && newCol < board.boardDim) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(newRow, newCol));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), promotionPiece));
            }
        }

        // West
        isCapture = false;
        for (int j = 1; j < range + 1; j++) {
            int newRow = row - j;
            int newCol = col - j;
            if (newRow > 0 && newCol > 0) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(newRow, newCol));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), promotionPiece));
            }
        }

        return chessMoveArrayList;
    }

    private Collection<ChessMove> checkStraight(ChessBoard board, ChessPosition myPosition, int range, ArrayList<ChessMove> chessMoveArrayList, ChessPiece.PieceType promotionPiece) {
        int row = myPosition.row;
        int col = myPosition.col;
        // Check north
        boolean isCapture = false;
        for (int i = 1; i < range + 1; i++) {
            int newRow = row + i;
            if (newRow < board.boardDim) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(newRow, col));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(newRow, col), promotionPiece));
            }
        }

        // South
        isCapture = false;
        for (int i = 1; i < range + 1; i++) {
            int newRow = row - i;
            if (newRow > 0) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(newRow, col));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(newRow, col), promotionPiece));
            }
        }

        // East
        isCapture = false;
        for (int j = 1; j < range + 1; j++) {
            int newCol = col + j;
            if (newCol < board.boardDim) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(row, newCol));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(row, newCol), promotionPiece));
            }
        }

        // West
        isCapture = false;
        for (int j = 1; j < range + 1; j++) {
            int newCol = col - j;
            if (newCol > 0) {
                if (isCapture) {
                    break;
                }
                // First time a piece is encountered, if it's the enemy set isCapture flag.
                ChessPiece potentialCapture = board.getPiece(new ChessPosition(row, newCol));
                if (potentialCapture != null) {
                    isCapture = true;
                    // If the piece is our team, break out of the loop without adding it as a valid move.
                    if (potentialCapture.getTeamColor() == this.getTeamColor()) break;
                }
                chessMoveArrayList.add(new ChessMove(myPosition, new ChessPosition(row, newCol), promotionPiece));
            }
        }

        return chessMoveArrayList;
    }

}
