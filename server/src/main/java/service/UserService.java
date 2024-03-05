package service;

import dataAccess.*;
import exceptions.BadRequestException;
import model.AuthData;
import model.RegisterUserResponse;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    public AuthData addUser(UserData user) throws DataAccessException, BadRequestException {
        UserData createdUser = new SQLUserDAO().createUser(user);
        UUID authtoken = new SQLAuthDAO().createAuth(createdUser.username());
        return new AuthData(authtoken, createdUser.username());
    }

    public AuthData loginUser(UserData user) throws DataAccessException, BadRequestException {
        // Check login info to make sure it is a registered user and the password is correct
        UserData checkUser = new SQLUserDAO().getUser(user.username());
        if (checkUser == null || !Objects.equals(checkUser.username(), user.username()) || !BCrypt.checkpw(user.password(), checkUser.password())) {
            throw new BadRequestException(401, "Error: unauthorized");
        }
        // Delete any old authtokens and create a new one to send back
        AuthDAO dao = new SQLAuthDAO();
        return new AuthData(dao.createAuth(user.username()), user.username());
    }

    public void clear() throws DataAccessException {
        UserDAO dao = new SQLUserDAO();
        dao.clearUsers();
    }

    public void logoutUser(AuthData authData) throws DataAccessException, BadRequestException {
        new SQLAuthDAO().deleteAuth(authData.authToken());
    }
}
