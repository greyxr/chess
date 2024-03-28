package webSocketMessages.userCommands;

import chess.ChessMove;

public record MakeMove(int gameId, ChessMove move) {
}
