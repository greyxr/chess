package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
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

    public String createGame(GameData gameData) {
        return null;
    }

    public Object joinGame(JoinGameRequest joinGameRequest, Set<String> headers) {
        return headers;
    }
}
