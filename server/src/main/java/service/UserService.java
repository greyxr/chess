package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;

import java.util.UUID;

public class UserService {
    public UUID addUser(UserData user) throws DataAccessException {
        UserData createdUser = new MemoryUserDAO().createUser(user);
        return new MemoryAuthDAO().createAuth(createdUser.username());
    }

    public void clear() throws DataAccessException {
        UserDAO dao = new MemoryUserDAO();
        dao.clearUsers();
    }
}
