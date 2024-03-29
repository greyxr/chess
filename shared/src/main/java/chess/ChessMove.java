package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    public ChessPosition startPosition;
    public ChessPosition endPosition;
    public ChessPiece.PieceType promotionPiece;

    private ChessPiece displacedPiece;
    private ChessPosition displacedPosition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(getStartPosition(), chessMove.getStartPosition()) && Objects.equals(getEndPosition(), chessMove.getEndPosition()) && getPromotionPiece() == chessMove.getPromotionPiece();
    }

    @Override
    public String toString() {
        return this.startPosition.row + ", " + this.startPosition.col + " => " + this.endPosition.row + ", " + this.endPosition.col + " : " + this.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartPosition(), getEndPosition(), getPromotionPiece());
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public void setDisplacedInfo(ChessPiece displacedPiece, ChessPosition displacedPosition) {
        this.displacedPiece = displacedPiece;
        this.displacedPosition = displacedPosition;
    }

    public ChessPiece getDisplacedPiece() {
        return displacedPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }
}
