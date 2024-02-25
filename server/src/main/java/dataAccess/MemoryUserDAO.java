package dataAccess;

import exceptions.BadRequestException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MemoryUserDAO implements UserDAO {
    static ArrayList<UserData> memoryUser = new ArrayList<>();

    @Override
    public void clearUsers() throws DataAccessException {
        memoryUser.clear();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : memoryUser) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException, BadRequestException {
        for (UserData currentUser : memoryUser) {
            if (Objects.equals(currentUser.username(), user.username()) || Objects.equals(currentUser.email(), user.email())) {
                throw new BadRequestException(403, "Error: User already exists");
            }
        }
        memoryUser.add(user);
        return user;
    }
}
