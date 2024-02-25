package handlers;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

import java.util.UUID;

public class UserHandler {
    public String clear(Request req) throws DataAccessException {
        // Make an instance of GameService for this endpoint
        UserService service = new UserService();
        GameService gameService = new GameService();
        AuthService authService = new AuthService();
        service.clear();
        gameService.clearGames();
        authService.clearAuth();
        return new Gson().toJson(null);
    }

    public String createUser(Request req) throws DataAccessException {
        UUID authtoken = new UserService().addUser(new Gson().fromJson(req.body(), UserData.class));
        return new Gson().toJson(authtoken);
    }
}
