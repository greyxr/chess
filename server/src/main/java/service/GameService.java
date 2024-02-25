package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import model.JoinGameRequest;

import java.util.Set;

public class GameService {
    public void clearGames() throws DataAccessException {
        GameDAO dao = new MemoryGameDAO();
        dao.clearGames();
    }
    public Object listGames() {
        return null;
    }

    public Object createGame(GameData gameData) {
        return null;
    }

    public Object joinGame(JoinGameRequest joinGameRequest, Set<String> headers) {
        return headers;
    }
}
