package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    int gameId;
    public Leave(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
    }
}
