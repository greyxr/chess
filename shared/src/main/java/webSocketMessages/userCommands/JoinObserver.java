package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand {
    int gameId;
    public JoinObserver(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}
