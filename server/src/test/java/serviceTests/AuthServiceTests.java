package serviceTests;

import dataAccess.*;
import exceptions.BadRequestException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests {
    private static AuthDAO authDAO;
    private static AuthService authService;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authService = new AuthService();
        authDAO.clearAuth();
    }

    @Test
    void checkValidAuth() throws DataAccessException, BadRequestException {
        UUID authtoken = authDAO.createAuth("test");
        AuthData checkAuth = new AuthData(authtoken, "test");
        assertEquals(checkAuth, authService.checkAuth(authtoken));
    }

    @Test
    void checkInvalidAuth() {
        try {
            authService.checkAuth(UUID.randomUUID());
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Error: unauthorized");
        }
    }

    @Test
    void clearTest() throws DataAccessException {
        UUID authOne = authDAO.createAuth("test");
        UUID authTwo = authDAO.createAuth("test2");
        UUID authThree = authDAO.createAuth("test3");

        assertEquals(new AuthData(authOne, "test"), authDAO.getAuth(authOne));
        assertEquals(new AuthData(authTwo, "test2"), authDAO.getAuth(authTwo));
        assertEquals(new AuthData(authThree, "test3"), authDAO.getAuth(authThree));

        authService.clearAuth();

        assertNull(authDAO.getAuth(authOne));
        assertNull(authDAO.getAuth(authTwo));
        assertNull(authDAO.getAuth(authThree));
    }
}
