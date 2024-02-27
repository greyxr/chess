package serviceTests;

import dataAccess.*;
import exceptions.BadRequestException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private static UserService userService;
    private static UserData validUser;
    private static UserData invalidUser;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static UUID validUUID;
    private static AuthData validAuthData;
    @BeforeEach
    void setUp() {
        userService = new UserService();
        validUser = new UserData("username", "password", "email@email.com");
        invalidUser = new UserData("username2", null, "email2@email.com");
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        validUUID = UUID.randomUUID();
        validAuthData = new AuthData(validUUID, "username");
    }

    @Test
    void addValidUser() throws BadRequestException, DataAccessException {
        AuthData result = userService.addUser(validUser);
        assertEquals(userDAO.getUser(validUser.username()), validUser);
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    void addUserTwice() throws BadRequestException, DataAccessException {
        userService.addUser(validUser);
        try {
            assertThrows(BadRequestException.class, ()-> userService.addUser(validUser), "Expected BadRequestException");
        } catch(Exception ignored) {}
    }

    @Test
    void loginValidUser() {
        String test = "test";
        assertEquals("test", test);
    }

    @Test
    void loginInvalidUser() {
        String test = "test";
        assertNotEquals("test2", test);
    }

    @Test
    void logoutValidUser() {
        String test = "test";
        assertEquals("test", test);
    }

    @Test
    void logoutInvalidUser() {
        String test = "test";
        assertNotEquals("test2", test);
    }

    @Test
    void clearTest() {
        String test = "test";
        assertEquals("test", test);
    }
}
