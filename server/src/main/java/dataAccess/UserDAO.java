package dataAccess;

import model.UserData;

public interface UserDAO {
    void clearUsers() throws DataAccessException;

    UserData insertUser() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    UserData createUser(String username, String password) throws DataAccessException;
}
