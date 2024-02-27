package handlers;

import com.google.gson.Gson;
import exceptions.BadRequestException;
import model.GameData;
import model.JoinGameRequest;
import model.ListGamesResponse;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class GameHandler {
    public String listGamesRequest(Request req, Response res) {
        try {
            Collection<GameData> gamesList = new GameService().listGames();
            return new Gson().toJson(new ListGamesResponse(gamesList));
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
    }

    public String createGameRequest(Request req, Response res) {
        try {
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

    public String joinGameRequest(Request req, Response res) {
        try {
            JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
            if (joinGameRequest.gameID() == 0) {
                throw new BadRequestException(400, "Error: Invalid gameID");
            }
            String playerColor = joinGameRequest.playerColor();
            if (playerColor != null && !playerColor.equalsIgnoreCase("white") && !playerColor.equalsIgnoreCase("black")) {
                throw new BadRequestException(400, "Error: Invalid playerColor");
            }
            new GameService().joinGame(joinGameRequest, UUID.fromString(req.headers("Authorization")));
            return new Gson().toJson(null);
        } catch (BadRequestException e) {
            return new ExceptionHandler().handleRequestError(e, res);
        } catch (Exception e) {
            return new ExceptionHandler().handleServerError(e, res);
        }
    }

}
