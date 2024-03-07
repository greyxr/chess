package handlers;
import dataAccess.DataAccessException;
import exceptions.BadRequestException;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

import java.util.UUID;

public class UserHandler {
    public String clear(Request req, Response res) throws DataAccessException {
        try {
            // Make an instance of GameService for this endpoint
            UserService service = new UserService();
            GameService gameService = new GameService();
            AuthService authService = new AuthService();
            service.clear();
            gameService.clearGames();
            authService.clearAuth();
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
        return new Gson().toJson(null);
    }

    public String createUser(Request req, Response res) throws DataAccessException, BadRequestException {
        try {
            UserData createUserRequest = new Gson().fromJson(req.body(), UserData.class);
            if (createUserRequest.username() == null || createUserRequest.password() == null || createUserRequest.email() == null) {
                throw new BadRequestException(400, "Error: Missing one or more parameters.");
            }
            AuthData response = new UserService().addUser(createUserRequest);
            return new Gson().toJson(response);
        } catch (BadRequestException e) {
            return new ExceptionHandler().handleRequestError(e, res);
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
    }

    public String loginRequest(Request req, Response res) throws DataAccessException, BadRequestException {
        try {
            UserData loginRequest = new Gson().fromJson(req.body(), UserData.class);
            if (loginRequest.username() == null || loginRequest.password() == null) {
                throw new BadRequestException(400, "Error: Missing one or more parameters.");
            }
            AuthData response = new UserService().loginUser(loginRequest);
            return new Gson().toJson(response);
        } catch (BadRequestException e) {
            return new ExceptionHandler().handleRequestError(e, res);
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
    }

    public String logoutRequest(Request req, Response res) {
        try {
            UUID authtoken = UUID.fromString(req.headers("Authorization"));
            new UserService().logoutUser(new AuthService().checkAuth(authtoken));
            return new Gson().toJson(null);
        } catch (BadRequestException e) {
            return new ExceptionHandler().handleRequestError(e, res);
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
    }
}
