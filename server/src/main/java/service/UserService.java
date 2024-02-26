package service;

import dataAccess.*;
import exceptions.BadRequestException;
import model.AuthData;
import model.RegisterUserResponse;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    public AuthData addUser(UserData user) throws DataAccessException, BadRequestException {
        UserData createdUser = new MemoryUserDAO().createUser(user);
        UUID authtoken = new MemoryAuthDAO().createAuth(createdUser.username());
        return new AuthData(authtoken, createdUser.username());
    }

    public AuthData loginUser(UserData user) throws DataAccessException, BadRequestException {
        // Check login info to make sure it is a registered user and the password is correct
        UserData checkUser = new MemoryUserDAO().getUser(user.username());
        if (checkUser == null || !Objects.equals(checkUser.username(), user.username()) || !Objects.equals(checkUser.password(), user.password())) {
            throw new BadRequestException(401, "Error: unauthorized");
        }
        // Delete any old authtokens and create a new one to send back
        AuthDAO dao = new MemoryAuthDAO();
        dao.deleteAuth(user.username());
        return new AuthData(dao.createAuth(user.username()), user.username());
    }

    public void clear() throws DataAccessException {
        UserDAO dao = new MemoryUserDAO();
        dao.clearUsers();
    }
}
