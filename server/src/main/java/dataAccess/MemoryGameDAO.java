package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    static Collection<GameData> memoryGame = new ArrayList<>();

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
        GameData newGame = (color.equalsIgnoreCase("white")) ? (new GameData(targetGame.gameID(), username, targetGame.blackUsername(), targetGame.gameName(), targetGame.game()))
                : new GameData(targetGame.gameID(), targetGame.whiteUsername(), username, targetGame.gameName(), targetGame.game());
        memoryGame.add(newGame);
    }

    @Override
    public int insertGame(String gameName) throws DataAccessException {
        int gameID = memoryGame.size() + 1;
        memoryGame.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public int getBiggestGameId() throws DataAccessException {
        GameData biggestGame = new GameData(0, null, null, null, null);
        for (GameData game : memoryGame) {
            if (game.gameID() > biggestGame.gameID()) {
                biggestGame = game;
            }
        }
        return biggestGame.gameID();
    }

    @Override
    public int saveGame(int gameId, ChessGame game) throws DataAccessException {
        return 0;
    }
}
