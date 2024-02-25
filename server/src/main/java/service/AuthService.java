package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;

public class AuthService {
    public Object login(AuthData authData) {
        return null;
    }

    public Object logout(AuthData authData) {
        return null;
    }

    public void clearAuth() throws DataAccessException {
        AuthDAO dao = new MemoryAuthDAO();
        dao.clearAuth();
    }
}
