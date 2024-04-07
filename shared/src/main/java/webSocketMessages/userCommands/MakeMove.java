package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    int gameID;
    ChessMove move;
    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public int getGameId() {
        return this.gameID;
    }

    public ChessMove getMove() {
        return this.move;
    }
}
