package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import exceptions.BadRequestException;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import java.util.ArrayList;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private static UserData validUser;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameService gameService;
    private static GameData validGame;

    private static GameDAO gameDAO;
    @BeforeEach
    void setUp() throws DataAccessException {
        validUser = new UserData("username", "password", "email@email.com");
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService();
        gameDAO = new MemoryGameDAO();
        validGame = new GameData(1, "white", "black", "testgame", new ChessGame());
        userDAO.clearUsers();
        authDAO.clearAuth();
        gameDAO.clearGames();
    }

    @Test
    void testClear() throws DataAccessException {
        gameDAO.insertGame(validGame.gameName());
        gameService.clearGames();
        assertNull(gameDAO.getGame(validGame.gameID()));
    }

    @Test
    void listEmptyGames() throws DataAccessException {
        ArrayList<GameData> expected = new ArrayList<>();
        assertEquals(expected, gameService.listGames());
    }

    @Test
    void listMultipleGames() throws DataAccessException {
        ArrayList<GameData> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int gameID = gameDAO.insertGame("Game " + i + 1);
            GameData currentGame = new GameData(gameID, null, null, "Game " + i + 1, new ChessGame());
            expected.add(currentGame);
        }
        assertEquals(expected, gameService.listGames());

    }

    @Test
    void createValidGame() throws DataAccessException {
        GameData gameID = gameService.createGame(validGame);
        assertEquals(gameDAO.getGame(validGame.gameID()).gameName(), validGame.gameName());
        assertEquals(gameID.gameID(), validGame.gameID());

    }

    @Test
    void createMultipleGames() throws DataAccessException {
        ArrayList<GameData> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GameData currentGame = new GameData(gameDAO.getBiggestGameId() + 1, null, null, "Game " + i + 1, new ChessGame());
            expected.add(currentGame);
            gameService.createGame(currentGame);
        }
        assertEquals(expected, gameDAO.getGames());
    }

    @Test
    void joinValidGame() throws DataAccessException, BadRequestException {
        gameDAO.insertGame(validGame.gameName());
        userDAO.createUser(validUser);
        UUID authtoken = authDAO.createAuth(validUser.username());

        gameService.joinGame(new JoinGameRequest("WHITE", validGame.gameID()), authtoken);
        GameData expected = new GameData(1, validUser.username(), null, validGame.gameName(), gameDAO.getGame(validGame.gameID()).game());
        assertEquals(expected, gameDAO.getGame(validGame.gameID()));
    }

    @Test
    void joinInvalidGameID() throws DataAccessException, BadRequestException {
        gameDAO.insertGame(validGame.gameName());
        userDAO.createUser(validUser);
        UUID authtoken = authDAO.createAuth(validUser.username());

        try {
            gameService.joinGame(new JoinGameRequest("WHITE", validGame.gameID() + 1), authtoken);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Error: Invalid gameID");
        }
    }

    @Test
    void joinGameAlreadyTaken() throws DataAccessException, BadRequestException {
        gameDAO.insertGame(validGame.gameName());
        UUID authtoken = authDAO.createAuth(validUser.username());
        UUID authtoken2 = authDAO.createAuth("user2");
        gameService.joinGame(new JoinGameRequest("WHITE", validGame.gameID()), authtoken);

        try {
            gameService.joinGame(new JoinGameRequest("WHITE", validGame.gameID()), authtoken2);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Error: already taken");
        }
    }

}
