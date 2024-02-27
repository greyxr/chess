package handlers;

import com.google.gson.Gson;
import exceptions.BadRequestException;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;
import java.util.UUID;

public class GameHandler {
    public String listGamesRequest(Request req, Response res) {
        try {
            // Check authtoken
//            UUID newAuthToken = new AuthService().refreshAuth(UUID.fromString(req.headers("Authorization")));
            new AuthService().checkAuth(UUID.fromString(req.headers("Authorization")));
            Collection<GameData> gamesList = new GameService().listGames();
            if (gamesList.isEmpty()) {
                return new Gson().toJson(null);
            }
            return new Gson().toJson(new GameService().listGames());
        } catch (BadRequestException e) {
            return new ExceptionHandler().handleRequestError(e, res);
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
    }

    public String createGameRequest(Request req, Response res) {
        try {
            // Check authtoken
            new AuthService().checkAuth(UUID.fromString(req.headers("Authorization")));
            GameData gameData = new Gson().fromJson(req.body(), GameData.class);
            if (gameData.gameName() == null) {
                throw new BadRequestException(400, "Error: Missing gameName");
            }
            return new Gson().toJson(new GameService().createGame(gameData));
        } catch (BadRequestException e) {
            return new ExceptionHandler().handleRequestError(e, res);
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
    }

}
