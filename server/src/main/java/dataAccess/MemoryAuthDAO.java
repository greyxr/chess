package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    static ArrayList<AuthData> memoryAuth = new ArrayList<>();

    @Override
    public void clearAuth() throws DataAccessException {
        memoryAuth.clear();
    }

    @Override
    public void deleteAuth(UUID authtoken) throws DataAccessException {
        memoryAuth.removeIf(auth -> Objects.equals(auth.authToken(), authtoken));
    }

    @Override
    public UUID createAuth(String username) throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        memoryAuth.add(new AuthData(uuid, username));
        return uuid;
    }

    @Override
    public AuthData getAuth(UUID authtoken) throws DataAccessException {
        for (AuthData auth : memoryAuth) {
            if (authtoken.equals(auth.authToken())) {
                return auth;
            }
        }

        return null;
    }
}
