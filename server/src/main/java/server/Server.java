package server;

import com.google.gson.Gson;
import exceptions.BadRequestException;
import handlers.ExceptionHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

import java.io.Reader;
import java.util.UUID;

import static spark.Spark.before;
import static spark.Spark.halt;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        before("/game", (request, response) -> {
            try {
                String checkAuth = request.headers("Authorization");
                if (checkAuth.length() != 36) {
                    throw new BadRequestException(401, "Error: unauthorized");
                }
                new AuthService().checkAuth(UUID.fromString(request.headers("Authorization")));
            } catch(BadRequestException e) {
                halt(401, new ExceptionHandler().handleRequestError(e, response));
            }
        });

        // Register user
        Spark.post("/user", (request, response) -> new UserHandler().createUser(request, response));
        // Login
        Spark.post("/session", (request, response) -> new UserHandler().loginRequest(request, response));
        // Logout
        Spark.delete("/session", (request, response) -> new UserHandler().logoutRequest(request, response));
        // List games
        Spark.get("/game", (request, response) -> new GameHandler().listGamesRequest(request, response));
        // Create game
        Spark.post("/game", (request, response) -> new GameHandler().createGameRequest(request, response));
        // Join game
        Spark.put("/game", (request, response) -> new GameHandler().joinGameRequest(request, response));
        // Clear db
        Spark.delete("/db", (request, response) -> new UserHandler().clear(request, response));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
