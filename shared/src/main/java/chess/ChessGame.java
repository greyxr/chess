package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public TeamColor teamColor;
    public ChessBoard board;

    public GameState state;

    public Deque<ChessMove> moveHistory = new ArrayDeque<>();

    public ChessGame() {
        this.teamColor = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.state = GameState.IN_PROGRESS;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public enum GameState {
        IN_PROGRESS,
        BLACK_WIN,
        WHITE_WIN
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamColor == chessGame.teamColor && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, getBoard());
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece potentialPiece = this.getBoard().getPiece(startPosition);
        if (potentialPiece == null) return null;
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = potentialPiece.pieceMoves(this.getBoard(), startPosition);

        for (ChessMove move : pieceMoves) {
                this.movePiece(move);
                if (!isInCheck(potentialPiece.getTeamColor()) && !isInCheckmate(potentialPiece.getTeamColor())) {
                    potentialMoves.add(move);
                }
                this.undoMove();
        }
        return potentialMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        TeamColor currentColor = this.getBoard().getPiece(move.getStartPosition()).getTeamColor();
        if (this.getTeamTurn() != currentColor) throw new InvalidMoveException("It is not your turn.");
        Collection<ChessMove> validMoves = this.validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) throw new InvalidMoveException("Move is invalid.");
        movePiece(move);
        this.setTeamTurn(currentColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
    }

    public void movePiece(ChessMove move) {
        ChessMove tempMove = new ChessMove(move.getStartPosition(), move.getEndPosition(), move.promotionPiece);
        ChessPosition endPosition = move.getEndPosition();
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece displacedPiece = this.getBoard().getPiece(endPosition);
        ChessPiece currentPiece = this.getBoard().getPiece(startPosition);
        // If displaced piece is not null, it should be an enemy piece
        if (displacedPiece != null) {
            tempMove.setDisplacedInfo(displacedPiece, endPosition);
        }
        if (move.getPromotionPiece() != null) {
            this.getBoard().addPiece(endPosition, new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece()));
        } else {
            this.getBoard().addPiece(endPosition, currentPiece);
        }
        this.getBoard().addPiece(startPosition, null);
        this.moveHistory.addFirst(tempMove);
    }

    public void undoMove() {
        if (this.moveHistory.peek() == null) return;
        ChessMove lastMove = this.moveHistory.pop();
        ChessPiece displacedPiece = lastMove.getDisplacedPiece();
        ChessPosition endPosition = lastMove.getEndPosition();
        ChessPosition startPosition = lastMove.getStartPosition();
        ChessPiece firstPiece = this.getBoard().getPiece(endPosition);
        this.getBoard().addPiece(startPosition, firstPiece);
        if (displacedPiece == null) {
            this.getBoard().addPiece(endPosition, null);
        } else {
            this.getBoard().addPiece(endPosition, new ChessPiece(displacedPiece.getTeamColor(), displacedPiece.getPieceType()));
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ArrayList<ChessPosition> enemyMoves = new ArrayList<>();
        ChessPosition kingPosition = null;
        board = this.getBoard();
        for (int i = 1; i < board.boardDim; i++) {
            for (int j = 1; j < board.boardDim; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) continue;
                if (piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
                    for (ChessMove move : pieceMoves) {
                        enemyMoves.add(move.endPosition);
                    }
                }
                if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                }
            }
        }

        if (kingPosition == null) return false; // Can't be in check if you have no king

        boolean check = false;
        for (ChessPosition chessPosition : enemyMoves) {
            if (chessPosition.getRow() == kingPosition.getRow() && chessPosition.getColumn() == kingPosition.getColumn()) {
                check = true;
                break;
            }
        }

        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;
        for (int i = 1; i < this.getBoard().boardDim; i++) {
            for (int j = 1; j < this.getBoard().boardDim; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = this.getBoard().getPiece(currentPosition);
                // Ignore pieces that aren't there or are on the enemy team
                if (currentPiece == null || currentPiece.getTeamColor() != teamColor) continue;
                Collection<ChessMove> currentPieceMoves = currentPiece.pieceMoves(this.getBoard(), currentPosition);
                for (ChessMove move : currentPieceMoves) {
                    movePiece(move);
                    boolean stillInCheck = isInCheck(teamColor);
                    undoMove();
                    if (!stillInCheck) return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        int boardSize = this.getBoard().boardDim;
        for (int i = 1; i < boardSize; i++) {
            for (int j = 1; j < boardSize; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = this.getBoard().getPiece(currentPosition);
                if (currentPiece == null || currentPiece.getTeamColor() != teamColor) continue;
                if (this.validMoves(currentPosition) != null && !this.validMoves(currentPosition).isEmpty()) return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    public String[][] convertToMatrix(String color) {
        ChessGame.TeamColor teamColor = (color.equalsIgnoreCase("white") ? TeamColor.WHITE : TeamColor.BLACK);
        ChessBoard board = getBoard();
        int boardDim = board.boardDim;
        String[][] matrixBoard = new String[boardDim][boardDim];
        for (int i = 1; i < boardDim; i++) {
            for (int j = 1; j < boardDim; j++) {
                ChessPiece potentialPiece = board.getPiece(new ChessPosition(i, j));
                if (potentialPiece != null && (potentialPiece.pieceColor == teamColor)) {
                    matrixBoard[i - 1][j - 1] = chessPieceToString(potentialPiece);
                }
            }
        }
        return matrixBoard;
    }

    public String chessPieceToString(ChessPiece piece) {
        if (piece == null) {
            return null;
        }
        return switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "p";
        };
    }
}
