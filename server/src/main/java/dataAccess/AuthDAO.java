package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void clearAuth() throws DataAccessException;
    void deleteAuth(String username) throws DataAccessException;

    void createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authtoken) throws DataAccessException;
}
