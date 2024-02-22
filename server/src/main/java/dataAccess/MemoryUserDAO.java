package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    static ArrayList<AuthData> memoryUser = new ArrayList<>();

    @Override
    public void clearUsers() throws DataAccessException {

    }

    @Override
    public UserData insertUser() throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public UserData createUser(String username, String password) throws DataAccessException {
        return null;
    }
}
