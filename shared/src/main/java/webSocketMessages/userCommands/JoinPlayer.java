package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
    int gameId;
    ChessGame.TeamColor playerColor;
    public JoinPlayer(String authToken, int gameId, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameId = gameId;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public int getGameId() {
        return this.gameId;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return this.playerColor;
    }
}