package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.ServerError;
import model.*;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.ArrayList;
import java.util.UUID;

public class ServerFacade {
    static String url;

    static WSClient ws;
    static ServerCalls serverCalls;
    public ServerFacade(int port) {
        String url = "localhost:" + port;
        ServerFacade.url = url;
        serverCalls = new ServerCalls("http://localhost:" + port);
        try {
            System.out.println("Setting up websocket...");
            ws = new WSClient();
        } catch (Exception e) {
            System.out.println("Exception found: " + e);
            throw new RuntimeException(e);
        }

    }

    public void clear() throws ServerError {
        serverCalls.clearDB();
    }

    public ListGamesResponse getGames(UUID authToken) throws ServerError {
        return serverCalls.getGames(authToken);
    }

    public void sendJoinRequest(ArrayList<GameData> currentGames, int gameNumber, String color, UUID authToken) throws ServerError {
        if (gameNumber > currentGames.size() || currentGames.get(gameNumber - 1) == null) {
            throw new ServerError(500, "Game not found by number: " + gameNumber);
        } else {
            serverCalls.joinGame(new JoinGameRequest(color, currentGames.get(gameNumber - 1).gameID()), authToken);
        }
    }

    public AuthData sendLoginRequest(UserData userData) throws ServerError {
        return serverCalls.loginRequest(userData);
    }

    public AuthData sendRegisterRequest(UserData userData) throws ServerError {
            return serverCalls.registerRequest(userData);
    }

    public void sendLogoutRequest(UUID authToken) throws ServerError {
        serverCalls.logoutRequest(authToken);
    }

    public GameData sendCreateGameRequest(GameName newGame, UUID authToken) throws ServerError {
        return serverCalls.createGameRequest(newGame, authToken);
    }

    public void sendWebSocketConnect(String msg) throws Exception {
        ws.send(msg);
    }
}
