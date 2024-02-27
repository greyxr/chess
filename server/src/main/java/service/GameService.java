package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import exceptions.BadRequestException;
import model.GameData;
import model.JoinGameRequest;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class GameService {
    public void clearGames() throws DataAccessException {
        GameDAO dao = new MemoryGameDAO();
        dao.clearGames();
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return new MemoryGameDAO().getGames();
    }

    public GameData createGame(GameData gameData) throws DataAccessException {
        GameDAO dao = new MemoryGameDAO();
        int gameID = dao.getBiggestGameId() + 1;
        dao.insertGame(gameID, gameData.gameName());
        return new GameData(gameID, null, null, null, null);
    }

    public void joinGame(JoinGameRequest joinGameRequest, UUID authtoken) throws DataAccessException, BadRequestException {
        MemoryGameDAO dao = new MemoryGameDAO();
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
                dao.insertUser(joinGameRequest.gameID(), new MemoryAuthDAO().getAuth(authtoken).username(), "WHITE");
                break;
            case "black":
                if (requestedGame.blackUsername() != null) {
                    throw new BadRequestException(403, "Error: already taken");
                }
                dao.insertUser(joinGameRequest.gameID(), new MemoryAuthDAO().getAuth(authtoken).username(), "BLACK");
                break;
        }

    }
}
