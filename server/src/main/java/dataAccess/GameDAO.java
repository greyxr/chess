package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clearGames() throws DataAccessException;

    Collection<GameData> getGames() throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    void insertUser(String gameID, String game, String color) throws DataAccessException;

    void deleteGame(String gameID) throws DataAccessException;


}
