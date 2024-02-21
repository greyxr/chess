package dataAccess;

import model.UserData;

public interface UserDAO {
    void clear();

    UserData insertUser();

    UserData getUser(String username);

    UserData createUser(String username, String password);
}
