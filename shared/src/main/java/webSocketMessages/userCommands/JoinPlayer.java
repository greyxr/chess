package webSocketMessages.userCommands;

import chess.ChessGame;

public record JoinPlayer(int gameId, ChessGame.TeamColor playerColor) {
}
