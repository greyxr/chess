package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public int boardDim = 9;
    public ChessPiece[][] board = new ChessPiece[boardDim][boardDim];
    public ChessBoard() {
        for (int i = 0; i < this.boardDim; i++) {
            for (int j = 0; j < this.boardDim; j++) {
                // Set board to null
                board[i][j] = null;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.row][position.col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.row][position.col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Set pawns
        for (int j = 1; j < this.boardDim; j++) {
            addPiece(new ChessPosition(2, j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        int[] setRow = {1, 8};
        for (int i : setRow) {
            ChessGame.TeamColor currentColor = (i == 1) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            addPiece(new ChessPosition(i,1), new ChessPiece(currentColor, ChessPiece.PieceType.ROOK));
            addPiece(new ChessPosition(i,2), new ChessPiece(currentColor, ChessPiece.PieceType.KNIGHT));
            addPiece(new ChessPosition(i,3), new ChessPiece(currentColor, ChessPiece.PieceType.BISHOP));
            addPiece(new ChessPosition(i,4), new ChessPiece(currentColor, ChessPiece.PieceType.QUEEN));
            addPiece(new ChessPosition(i,5), new ChessPiece(currentColor, ChessPiece.PieceType.KING));
            addPiece(new ChessPosition(i,6), new ChessPiece(currentColor, ChessPiece.PieceType.BISHOP));
            addPiece(new ChessPosition(i,7), new ChessPiece(currentColor, ChessPiece.PieceType.KNIGHT));
            addPiece(new ChessPosition(i,8), new ChessPiece(currentColor, ChessPiece.PieceType.ROOK));
        }
    }
}
