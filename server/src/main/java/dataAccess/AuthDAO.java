package dataAccess;

import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    void clearAuth() throws DataAccessException;

    void deleteAuth(UUID authtoken) throws DataAccessException;

    UUID createAuth(String username) throws DataAccessException;

    AuthData getAuth(UUID authtoken) throws DataAccessException;
}
