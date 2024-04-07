package webSocketMessages.serverMessages;

import chess.ChessGame;

public class LoadGame extends ServerMessage {
    public ChessGame game;
    public LoadGame (ChessGame game, ServerMessageType type) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }
}
