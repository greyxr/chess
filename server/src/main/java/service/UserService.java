package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import exceptions.BadRequestException;
import model.RegisterUserResponse;
import model.UserData;

import java.util.UUID;

public class UserService {
    public RegisterUserResponse addUser(UserData user) throws DataAccessException, BadRequestException {
        UserData createdUser = new MemoryUserDAO().createUser(user);
        UUID authtoken = new MemoryAuthDAO().createAuth(createdUser.username());
        return new RegisterUserResponse(createdUser.username(), authtoken);
    }

    public void clear() throws DataAccessException {
        UserDAO dao = new MemoryUserDAO();
        dao.clearUsers();
    }
}
