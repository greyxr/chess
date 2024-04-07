package server;

import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.*;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.*;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

@WebSocket
public class WSServer {
    Map<Integer, Map<String, Session>> gameSessions;


    Gson gson;
    GameDAO gameDAO;
    AuthDAO authDAO;
    UserDAO userDAO;

    public WSServer() {
        gameSessions = new HashMap<>();
        gson = new Gson();
        try {
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
            userDAO = new SQLUserDAO();
        } catch (DataAccessException ex) {
            System.out.println("Failed to initalize DAOs in WSServer.");
            System.exit(-1);
        }

    }
    public static void main(String[] args) {
//        Spark.port(8080);
//        Spark.webSocket("/connect", WSServer.class);
//        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        CommandAdapter commandAdapter = new CommandAdapter();
        UserGameCommand command = commandAdapter.fromJson(msg);
//        session.getRemote().sendString("Command received: " + command.getCommandType());
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> join(session, (JoinPlayer) command);
                case JOIN_OBSERVER -> observe(session, (JoinObserver) command);
                case MAKE_MOVE -> move(session, (MakeMove) command);
                case LEAVE -> leave(session, (Leave) command);
                case RESIGN -> resign(session, (Resign) command);
            }
    }

    public void addGame(int gameId) {
        if (gameSessions.get(gameId) == null) {
            HashMap<String, Session> newGame = new HashMap<>();
            gameSessions.put(gameId, newGame);
        }
    }

    public String getAuthToken(int gameId, Session session) {
        for (Map.Entry<String, Session> entry : (gameSessions.get(gameId)).entrySet()) {
            if (entry.getValue() == session) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void join(Session conn, JoinPlayer command) throws IOException, DataAccessException {
//        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
//        conn.getRemote().sendString(gson.toJson(serverMessage));
        // Add game to map
        int gameId = command.getGameId();
        addGame(gameId);

        // Get user info
        UUID authToken = null;
        try {
            authToken = UUID.fromString(command.getAuthString());
        } catch (IllegalArgumentException e) {
            sendError(conn, "Invalid auth");
            return;
        }

        AuthData authData;
        UserData callingUser;
        GameData currentGame;
        try {
            authData = authDAO.getAuth(authToken);
            callingUser = userDAO.getUser(authData.username());
            // Get game from database
            currentGame = gameDAO.getGame(gameId);
        } catch (DataAccessException ex) {
            sendError(conn, "Error retrieving information game or user information. " + ex.getMessage());
            return;
        }


        if (callingUser == null || currentGame == null) {
            sendError(conn, "Unknown user or game");
            return;
        }

        // Verify and add user to gameId map
        ChessGame.TeamColor playerColor = command.getPlayerColor();
        String calledUser = null;
        switch (playerColor) {
            case ChessGame.TeamColor.WHITE -> calledUser = currentGame.whiteUsername();
            case ChessGame.TeamColor.BLACK -> calledUser = currentGame.blackUsername();
        }

        if (!Objects.equals(callingUser.username(), calledUser)) {
            sendError(conn, "Unknown user");
            return;
        }

        (gameSessions.get(gameId)).put(command.getAuthString(), conn);

        // Notify users
        ServerMessage serverMessage = new Notification(callingUser.username() + " has joined game " + gameId + " as " + command.getPlayerColor(), ServerMessage.ServerMessageType.NOTIFICATION);
        for (Map.Entry<String, Session> entry : (gameSessions.get(gameId)).entrySet()) {
            if (entry.getValue() != conn) {
                System.out.println("Connection found");
                send(entry.getValue(), serverMessage);
            }
        }

        // Send response
        ServerMessage loadGame = new LoadGame(currentGame.game(), ServerMessage.ServerMessageType.LOAD_GAME);
        send(conn, loadGame);
    }

    public void observe(Session conn, JoinObserver command) throws IOException {
//        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
//        conn.getRemote().sendString(gson.toJson(serverMessage));

        // Add game to map
        int gameId = command.getGameId();
        addGame(gameId);

        // Get user info
        UUID authToken = null;
        try {
            authToken = UUID.fromString(command.getAuthString());
        } catch (IllegalArgumentException e) {
            sendError(conn, "Invalid auth");
            return;
        }
        AuthData authData;
        UserData callingUser;
        GameData currentGame;
        try {
            authData = authDAO.getAuth(authToken);
            callingUser = userDAO.getUser(authData.username());
            // Get game from database
            currentGame = gameDAO.getGame(gameId);
        } catch (DataAccessException ex) {
            sendError(conn, "Error retrieving information game or user information. " + ex.getMessage());
            return;
        }


        if (callingUser == null || currentGame == null) {
            sendError(conn, "Unknown user or game");
            return;
        }

        // Verify and add user to gameId map
        (gameSessions.get(gameId)).put(command.getAuthString(), conn);

        // Notify users
        ServerMessage serverMessage = new Notification(callingUser.username() + " has joined game " + gameId + " as an observer.", ServerMessage.ServerMessageType.NOTIFICATION);
        for (Map.Entry<String, Session> entry : (gameSessions.get(gameId)).entrySet()) {
            if (entry.getValue() != conn) {
                System.out.println("Connection found");
                send(entry.getValue(), serverMessage);
            }
        }

        // Send response
        ServerMessage loadGame = new LoadGame(currentGame.game(), ServerMessage.ServerMessageType.LOAD_GAME);
        send(conn, loadGame);
    }

    public void move(Session conn, MakeMove command) throws IOException {
        // Get game from map
        int gameId = command.getGameId();
        String authTokenString = getAuthToken(gameId, conn);
        if (authTokenString == null) {
            sendError(conn, "Unknown user");
            return;
        }

        // Get user info
        UUID authToken = UUID.fromString(authTokenString);
        AuthData authData;
        UserData callingUser;
        GameData currentGame;
        try {
            authData = authDAO.getAuth(authToken);
            callingUser = userDAO.getUser(authData.username());
            // Get game from database
            currentGame = gameDAO.getGame(gameId);
        } catch (DataAccessException ex) {
            sendError(conn, "Error retrieving information game or user information. " + ex.getMessage());
            return;
        }


        if (callingUser == null || currentGame == null) {
            sendError(conn, "Unknown user or game");
            return;
        }

        // Verify user is in the game
        String whitePlayer = currentGame.whiteUsername();
        String blackPlayer = currentGame.blackUsername();
        if (!Objects.equals(whitePlayer, callingUser.username()) && !Objects.equals(blackPlayer, callingUser.username())) {
            sendError(conn, "Unknown player.");
            return;
        }

        // Verify turn color
        ChessGame.TeamColor playerColor = Objects.equals(whitePlayer, callingUser.username()) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if (playerColor != currentGame.game().getTeamTurn()) {
            sendError(conn, "It is not your turn.");
            return;
        }

        // Make sure the piece is the right color
        ChessPiece potentialPiece = currentGame.game().getBoard().getPiece(command.getMove().startPosition);
        if (potentialPiece == null || potentialPiece.getTeamColor() != playerColor) {
            sendError(conn, "Invalid piece.");
            return;
        }

        // Make move
        try {
            currentGame.game().makeMove(command.getMove());
        } catch (chess.InvalidMoveException ex) {
            sendError(conn, ex.getMessage());
            return;
        }

        try {
            gameDAO.saveGame(gameId, currentGame.game());
        } catch(DataAccessException ex) {
            sendError(conn, "Unable to save game.");
            return;
        }


        // Notify users and send response
        ServerMessage moveNotification = new Notification(callingUser.username() + " has made a move.", ServerMessage.ServerMessageType.NOTIFICATION);
        ServerMessage loadGameMessage = new LoadGame(currentGame.game(), ServerMessage.ServerMessageType.LOAD_GAME);
        for (Map.Entry<String, Session> entry : (gameSessions.get(gameId)).entrySet()) {
            if (entry.getValue() != conn) {
                System.out.println("Connection found");
                send(entry.getValue(), moveNotification);
            }
            send(entry.getValue(), loadGameMessage);
        }
    }

    public void leave(Session conn, Leave command) throws IOException {
//        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
//        conn.getRemote().sendString(gson.toJson(serverMessage));
    }

    public void resign(Session conn, Resign command) throws IOException {
//        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
//        conn.getRemote().sendString(gson.toJson(serverMessage));
    }

    public void sendError(Session conn, String msg) throws IOException {
        ServerMessage message = new Error("Error: " + msg, ServerMessage.ServerMessageType.ERROR);
        send(conn, message);
    }

    public void send(Session conn, ServerMessage msg) throws IOException {
        String json = gson.toJson(msg);
        conn.getRemote().sendString(json);
    }

    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("Websocket error!");
        System.out.println(throwable.toString());
    }

    @OnWebSocketClose
    public void onClose(Session session, int var2, String var3) {
        System.out.println("On close. var2: " + var2 + " var3: " + var3);
        // Iterate over the entries of the outer map
        for (Map.Entry<Integer, Map<String, Session>> outerEntry : gameSessions.entrySet()) {
            // Get the inner map associated with the current key
            Map<String, Session> innerMap = outerEntry.getValue();

            // Create an iterator to safely remove sessions from the inner map
            Iterator<Map.Entry<String, Session>> iterator = innerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Session> innerEntry = iterator.next();
                Session currentSession = innerEntry.getValue();
                if (currentSession.equals(session)) {
                    // Remove the session from the inner map
                    System.out.println("Found connection!");
                    iterator.remove();
                }
            }
        }
    }

}