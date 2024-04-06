package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clearGames() throws DataAccessException;

    Collection<GameData> getGames() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void insertUser(int gameID, String username, String color) throws DataAccessException;

    int insertGame(String gameName) throws DataAccessException;

    int getBiggestGameId() throws DataAccessException;

    int saveGame(int gameId, ChessGame game) throws DataAccessException;


}
