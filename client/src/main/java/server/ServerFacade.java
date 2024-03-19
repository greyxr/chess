package server;

import exceptions.ServerError;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerFacade {
    static String url;
    static ServerCalls serverCalls;
    public ServerFacade(int port) {
        String url = "localhost:" + port;
        ServerFacade.url = url;
        serverCalls = new ServerCalls("http://localhost:8080");

    }

    public ListGamesResponse getGames(UUID authToken) throws ServerError {
        return serverCalls.getGames(authToken);
    }

    public ArrayList<GameData> sendJoinRequest(int gameNumber, String color, UUID authToken) throws ServerError {
        ArrayList<GameData> currentGames = new ArrayList<>();
        currentGames.addAll(serverCalls.getGames(authToken).games());
        if (gameNumber > currentGames.size() || currentGames.get(gameNumber - 1) == null) {
            throw new ServerError(500, "Game not found by number: " + gameNumber);
        } else {
            serverCalls.joinGame(new JoinGameRequest(color, currentGames.get(gameNumber - 1).gameID()), authToken);
        }
        return currentGames;
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
}
