package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import exceptions.BadRequestException;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    public void clearAuth() throws DataAccessException {
        AuthDAO dao = new MemoryAuthDAO();
        dao.clearAuth();
    }

    public AuthData checkAuth(UUID authtoken) throws DataAccessException, BadRequestException {
        AuthDAO dao = new MemoryAuthDAO();
        AuthData checkAuth = dao.getAuth(authtoken);
        if (checkAuth == null) {
            throw new BadRequestException(401, "Error: unauthorized");
        }
        return checkAuth;
    }
}
