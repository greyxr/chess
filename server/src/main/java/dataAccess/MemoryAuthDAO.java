package dataAccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO {
    static ArrayList<AuthData> memoryAuth = new ArrayList<>();

    @Override
    public void clearAuth() throws DataAccessException {

    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {

    }

    @Override
    public void createAuth(String username) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authtoken) throws DataAccessException {
        return null;
    }
}
