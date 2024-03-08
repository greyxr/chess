package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import exceptions.BadRequestException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseAccessTests {
    static UserDAO userDAO;
    static UserData testUser;
    static GameData testGame;
    static GameDAO gameDAO;

    static AuthDAO authDAO;

    static AuthData authData;

    @BeforeAll
    static void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        testUser = new UserData("test", "password", "email@email.com");
        gameDAO = new SQLGameDAO();
        testGame = new GameData(1, "white", "black", "gameName", new ChessGame());
        authDAO = new SQLAuthDAO();
        authData = new AuthData(UUID.randomUUID(), "user");
    }

    @BeforeEach
    void eachSetup() throws DataAccessException {
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuth();
    }

    @Test
    void testAddGetUser() throws BadRequestException, DataAccessException {
        assertEquals(testUser, userDAO.createUser(testUser));
        assertEquals(testUser.username(), userDAO.getUser(testUser.username()).username());
    }

    @Test
    void testAddInvalidUser() throws BadRequestException, DataAccessException {
        userDAO.createUser(testUser);
        try {
            userDAO.createUser(testUser);
        } catch (Exception e) {
            assertEquals(DataAccessException.class, e.getClass());
        }
    }

    @Test
    void testGetInvalidUser() throws DataAccessException {
        assertNull(userDAO.getUser("test2"));
    }

    @Test
    void testClearUsers() throws DataAccessException, BadRequestException {
        userDAO.createUser(testUser);
        userDAO.clearUsers();
        assertNull(userDAO.getUser("test"));
    }

    // GAMEDAO TESTS

    @Test
    void testAddGame() throws DataAccessException {
        int gameID = gameDAO.insertGame(testGame.gameName());
        assertEquals(testGame.gameName(), gameDAO.getGame(gameID).gameName());
    }

    @Test
    void testGetInvalidGame() throws DataAccessException {
        assertNull(gameDAO.getGame(1));
    }

    @Test
    void testAddInvalidGame() throws DataAccessException {
        try {
            gameDAO.insertGame(null);
        } catch (Exception e) {
            assertEquals(DataAccessException.class, e.getClass());
        }
    }

    @Test
    void testInsertUser() throws DataAccessException {
        int gameID = gameDAO.insertGame(testGame.gameName());
        gameDAO.insertUser(gameID, "testUser", "white");
        assertEquals("testUser", gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    void testGetSingleGame() throws DataAccessException {
        gameDAO.insertGame("game1");
        ArrayList<GameData> results = (ArrayList<GameData>) gameDAO.getGames();
        assertEquals("game1", results.getFirst().gameName());
    }

    @Test
    void testGetGames() throws DataAccessException {
        gameDAO.insertGame("game1");
        gameDAO.insertGame("game2");
        gameDAO.insertGame("game3");

        assertEquals(3, gameDAO.getGames().size());
    }

    @Test
    void testGetNoGames() throws DataAccessException {
        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    void clearGames() throws DataAccessException {
        int one = gameDAO.insertGame("game1");
        int two = gameDAO.insertGame("game2");
        int three = gameDAO.insertGame("game3");

        gameDAO.clearGames();

        assertNull(gameDAO.getGame(one));
        assertNull(gameDAO.getGame(two));
        assertNull(gameDAO.getGame(three));

        assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    void clearAuth() throws DataAccessException {
        UUID one = authDAO.createAuth("test1");
        UUID two = authDAO.createAuth("test2");
        UUID three = authDAO.createAuth("test3");

        authDAO.clearAuth();

        assertNull(authDAO.getAuth(one));
        assertNull(authDAO.getAuth(two));
        assertNull(authDAO.getAuth(three));
    }

    @Test
    void createAuth() throws DataAccessException {
        assertEquals(UUID.class, authDAO.createAuth("user").getClass());
    }

    @Test
    void createInvalidAuth() {
        try {
            authDAO.createAuth(null);
        } catch (Exception e) {
            assertEquals(DataAccessException.class, e.getClass());
        }
    }

    @Test
    void getAuth() throws DataAccessException {
        UUID authToken = authDAO.createAuth("user");
        assertEquals(new AuthData(authToken, "user"), authDAO.getAuth(authToken));
    }

    @Test
    void getInvalidAuth() throws DataAccessException {
        assertNull(authDAO.getAuth(UUID.randomUUID()));
    }

    @Test
    void deleteAuth() throws DataAccessException {
        UUID authToken = authDAO.createAuth("user");
        authDAO.deleteAuth(authToken);
        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    void deleteInvalidAuth() {
        try {
            authDAO.deleteAuth(null);
        } catch (Exception e) {
            assertEquals(DataAccessException.class, e.getClass());
        }
    }
}
