package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    static ArrayList<GameData> memoryGame = new ArrayList<>();

    @Override
    public void clearGames() throws DataAccessException {
        memoryGame.clear();

    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        return memoryGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : memoryGame) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void insertUser(int gameID, String username, String color) throws DataAccessException {
        GameData targetGame = null;
        for (GameData currentGame : memoryGame) {
            if (currentGame.gameID() == gameID) {
                targetGame = currentGame;
            }
        } if (targetGame == null) {
            return;
        }

        memoryGame.remove(targetGame);
        GameData newGame = (color.equalsIgnoreCase("white")) ? (new GameData(targetGame.gameID(), targetGame.whiteUsername(), username, targetGame.gameName(), targetGame.game()))
                : new GameData(targetGame.gameID(), username, targetGame.blackUsername(), targetGame.gameName(), targetGame.game());
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        memoryGame.removeIf(game -> Objects.equals(game.gameID(), gameID));
    }
}
