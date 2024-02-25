package dataAccess;

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
    public UserData createUser(UserData user) throws DataAccessException {
        memoryUser.removeIf(currentUser -> Objects.equals(currentUser, user));
        memoryUser.add(user);
        return user;
    }
}
