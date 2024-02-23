package service;

import model.GameData;
import model.JoinGameRequest;

import java.util.Set;

public class GameService {
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
