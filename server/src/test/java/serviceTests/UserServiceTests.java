package serviceTests;

import dataAccess.*;
import exceptions.BadRequestException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
    @BeforeEach
    void setUp() throws DataAccessException {
        userService = new UserService();
        validUser = new UserData("username", "password", "email@email.com");
        invalidUser = new UserData("username2", null, "email2@email.com");
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        validUUID = UUID.randomUUID();
        userDAO.clearUsers();
        authDAO.clearAuth();
    }

    @Test
    void addValidUser() throws BadRequestException, DataAccessException {
        AuthData result = userService.addUser(validUser);
        UserData user = userDAO.getUser(validUser.username());
        assertEquals(user.username(), validUser.username());
        assertTrue(BCrypt.checkpw(validUser.password(), user.password()));

        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    void addUserTwice() throws BadRequestException, DataAccessException {
        userService.addUser(validUser);
        try {
            userService.addUser(validUser);
        } catch(Exception e) {
            String message = e.getMessage();
            assertEquals(message, "Error: User already exists");
        }
    }

    @Test
    void loginValidUser() throws BadRequestException, DataAccessException {
        userService.addUser(validUser);
        AuthData authData = userService.loginUser(validUser);
        AuthData checkAuth = authDAO.getAuth(authData.authToken());
        assertEquals(authData, checkAuth);
        assertNotNull(authData);
        assertNotNull(checkAuth);
    }

    @Test
    void loginInvalidUser() {
        try {
            userService.addUser(validUser);
            userService.loginUser(invalidUser);
        } catch (BadRequestException | DataAccessException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    void logoutValidUser() throws BadRequestException, DataAccessException {
        userService.addUser(validUser);
        AuthData authData = userService.loginUser(validUser);
        userService.logoutUser(authData);
        assertNull(authDAO.getAuth(authData.authToken()));

    }

    @Test
    void logoutInvalidUser() throws DataAccessException {
        assertNull(authDAO.getAuth(validUUID));
    }

    @Test
    void clearTest() throws DataAccessException, BadRequestException {
        userService.addUser(validUser);
        userService.clear();
        assertNull(userDAO.getUser(validUser.username()));
    }
}
