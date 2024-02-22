package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO {
    static ArrayList<AuthData> memoryGame = new ArrayList<>();

    @Override
    public void clearGames() throws DataAccessException {
        throw new DataAccessException("error");

    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void insertUser(String gameID, String game, String color) throws DataAccessException {

    }

    @Override
    public void deleteGame(String gameID) throws DataAccessException {

    }
}
