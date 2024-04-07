package service;

import dataAccess.*;
import exceptions.BadRequestException;
import model.AuthData;
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
        int gameID = dao.insertGame(gameData.gameName());
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
        AuthDAO authDAO = new SQLAuthDAO();
        AuthData currentUser = authDAO.getAuth(authtoken);
        switch (joinGameRequest.playerColor().toLowerCase()) {
            case "white":
                if (requestedGame.whiteUsername() != null && !requestedGame.whiteUsername().equals(currentUser.username())) {
                    throw new BadRequestException(403, "Error: already taken");
                }
                dao.insertUser(joinGameRequest.gameID(), new SQLAuthDAO().getAuth(authtoken).username(), "WHITE");
                break;
            case "black":
                if (requestedGame.blackUsername() != null && !requestedGame.blackUsername().equals(currentUser.username())) {
                    throw new BadRequestException(403, "Error: already taken");
                }
                dao.insertUser(joinGameRequest.gameID(), new SQLAuthDAO().getAuth(authtoken).username(), "BLACK");
                break;
        }

    }
}
