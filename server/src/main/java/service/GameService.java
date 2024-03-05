package service;

import dataAccess.*;
import exceptions.BadRequestException;
import model.GameData;
import model.JoinGameRequest;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class GameService {
    public void clearGames() throws DataAccessException {
        GameDAO dao = new SQLGameDAO();
        dao.clearGames();
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return new SQLGameDAO().getGames();
    }

    public GameData createGame(GameData gameData) throws DataAccessException {
        GameDAO dao = new SQLGameDAO();
        int gameID = dao.getBiggestGameId() + 1;
        dao.insertGame(gameID, gameData.gameName());
        return new GameData(gameID, null, null, null, null);
    }

    public void joinGame(JoinGameRequest joinGameRequest, UUID authtoken) throws DataAccessException, BadRequestException {
        GameDAO dao = new SQLGameDAO();
        GameData requestedGame = dao.getGame(joinGameRequest.gameID());
        if (requestedGame == null) {
            throw new BadRequestException(400, "Error: Invalid gameID");
        }
        if (joinGameRequest.playerColor() == null) {
            return;
        }
        switch (joinGameRequest.playerColor().toLowerCase()) {
            case "white":
                if (requestedGame.whiteUsername() != null) {
                    throw new BadRequestException(403, "Error: already taken");
                }
                dao.insertUser(joinGameRequest.gameID(), new SQLAuthDAO().getAuth(authtoken).username(), "WHITE");
                break;
            case "black":
                if (requestedGame.blackUsername() != null) {
                    throw new BadRequestException(403, "Error: already taken");
                }
                dao.insertUser(joinGameRequest.gameID(), new SQLAuthDAO().getAuth(authtoken).username(), "BLACK");
                break;
        }

    }
}
