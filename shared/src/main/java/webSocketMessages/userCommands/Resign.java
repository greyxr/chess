package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    int gameID;
    public Resign(String authToken, int gameId) {
        super(authToken);
        this.gameID = gameId;
        this.commandType = CommandType.RESIGN;
    }
}
