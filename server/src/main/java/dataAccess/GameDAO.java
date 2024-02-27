package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clearGames() throws DataAccessException;

    Collection<GameData> getGames() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void insertUser(int gameID, String username, String color) throws DataAccessException;

    void insertGame(int gameID, String gameName) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

    int getBiggestGameId() throws DataAccessException;


}
