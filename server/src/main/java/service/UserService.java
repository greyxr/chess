package service;

import com.google.gson.Gson;
import model.UserData;
import spark.*;

public class UserService {
    public UserData addUser(UserData user) {
        return user;
    }

    public Object clear() {
        return null;
    }
}
