package server;

import com.google.gson.Gson;
import handlers.UserHandler;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

import java.io.Reader;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register user
        Spark.post("/user", (request, response) -> new UserHandler().createUser(request, response));
        // Login
        Spark.post("/session", (request, response) -> new UserHandler().loginRequest(request, response));
        // Logout
        Spark.delete("/session", (request, response) -> new UserHandler().logoutRequest(request, response));
        // List games
        Spark.get("/game", (request, response) -> new GameService().listGames());
        // Create game
        Spark.post("/game", (request, response) -> new GameService().createGame(new Gson().fromJson(request.body(), GameData.class)));
        // Join game
        Spark.put("/game", (request, response) -> new Gson().toJson(new GameService().joinGame(new Gson().fromJson(request.body(), JoinGameRequest.class), request.headers())));
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
