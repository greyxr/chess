package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import exceptions.BadRequestException;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    public void clearAuth() throws DataAccessException {
        AuthDAO dao = new SQLAuthDAO();
        dao.clearAuth();
    }

    public AuthData checkAuth(UUID authtoken) throws DataAccessException, BadRequestException {
        AuthDAO dao = new SQLAuthDAO();
        AuthData checkAuth = dao.getAuth(authtoken);
        if (checkAuth == null) {
            throw new BadRequestException(401, "Error: unauthorized");
        }
        return checkAuth;
    }
}
