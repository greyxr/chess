package clientTests;

import exceptions.ServerError;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;
import server.ServerFacade;
import java.util.ArrayList;
import java.util.UUID;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static UserData validUser;
    static UserData invalidUser;
    static GameData validGame;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        validUser = new UserData("testUser", "password", "email");
        invalidUser = new UserData("invalidUser", null, null);
        validGame = new GameData(1, null, null, "testGame", null);
    }

    @BeforeEach
    public void clearServer() throws ServerError {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testValidRegister() throws ServerError {
        AuthData results = facade.sendRegisterRequest(validUser);
        assertEquals(36, results.authToken().toString().length());
    }

    @Test
    public void testInvalidRegister() {
        try {
            facade.sendRegisterRequest(invalidUser);
        } catch (Exception e) {
            assertEquals("Bad Request", e.getMessage());
        }
    }

    @Test
    public void testDuplicateRegister() {
        try {
            facade.sendRegisterRequest(validUser);
            facade.sendRegisterRequest(validUser);
        } catch (Exception e) {
            assertEquals("Forbidden", e.getMessage());
        }
    }

    @Test
    public void testValidLogin() throws ServerError {
        facade.sendRegisterRequest(validUser);
        AuthData results = facade.sendLoginRequest(validUser);
        assertEquals(36, results.authToken().toString().length());
    }

    @Test
    public void testInvalidLogin() {
        try {
            facade.sendLoginRequest(validUser);
        } catch (Exception e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testValidLogout() throws ServerError {
        AuthData results = facade.sendRegisterRequest(validUser);
        facade.sendLogoutRequest(results.authToken());
        try {
            facade.sendLogoutRequest(results.authToken());
        } catch (Exception e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testInvalidLogout() {
        try {
            facade.sendLogoutRequest(UUID.randomUUID());
        } catch (Exception e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testCreateValidGame() throws ServerError {
        AuthData authData = facade.sendRegisterRequest(validUser);
        GameData testGame = facade.sendCreateGameRequest(new GameName("testGame"), authData.authToken());
        assertEquals(1, testGame.gameID());
    }

    @Test
    public void testCreateInvalidGame() {
        UUID authToken;
        try {
            authToken = facade.sendRegisterRequest(validUser).authToken();
            facade.sendCreateGameRequest(new GameName(null), authToken);
        } catch (Exception e) {
            assertEquals("Bad Request", e.getMessage());
        }

        try {
            facade.sendCreateGameRequest(new GameName("testGame"), null);
        } catch (Exception e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testListGames() throws ServerError {
        AuthData authData = facade.sendRegisterRequest(validUser);
        ListGamesResponse currentGames = facade.getGames(authData.authToken());
        assertEquals(0, currentGames.games().size());
        facade.sendCreateGameRequest(new GameName("testGame"), authData.authToken());
        currentGames = facade.getGames(authData.authToken());
        assertEquals(1, currentGames.games().size());
    }

    @Test
    public void testInvalidListGames() {
        try {
            facade.getGames(UUID.randomUUID());
        } catch (Exception e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testJoinValidGame() throws ServerError {
        AuthData authData = facade.sendRegisterRequest(validUser);
        facade.sendCreateGameRequest(new GameName("testGame"), authData.authToken());
        ArrayList<GameData> currentGames = new ArrayList<>(facade.getGames(authData.authToken()).games());
        facade.sendJoinRequest(currentGames, 1, "white", authData.authToken());
        currentGames.clear();
        currentGames.addAll(facade.getGames(authData.authToken()).games());
        assertEquals(validUser.username(), currentGames.getFirst().whiteUsername());
    }

    @Test
    public void testJoinInvalidGame() {
        try {
            AuthData authData = facade.sendRegisterRequest(validUser);
            ArrayList<GameData> currentGames = new ArrayList<>();
            facade.sendJoinRequest(currentGames, 1, "white", authData.authToken());
        } catch (Exception e) {
            assertEquals("Game not found by number: 1", e.getMessage());
        }
    }
}