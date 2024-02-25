package dataAccess;

import model.UserData;

public interface UserDAO {
    void clearUsers() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    UserData createUser(UserData user) throws DataAccessException;
}
