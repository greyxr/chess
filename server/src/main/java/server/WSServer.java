package server;

import com.google.gson.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

@WebSocket
public class WSServer {

    Map<String, Session> sessions;
    Map<Session, Integer> gameSessions;

    Gson gson;

    public WSServer() {
        sessions = new HashMap<>();
        gameSessions = new HashMap<>();
        gson = new Gson();
    }
    public static void main(String[] args) {
//        Spark.port(8080);
//        Spark.webSocket("/connect", WSServer.class);
//        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    public Session getConnection(String authToken, Session session) {
        return sessions.get(authToken);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        CommandAdapter commandAdapter = new CommandAdapter();
        UserGameCommand command = commandAdapter.fromJson(msg);
        session.getRemote().sendString("Command received: " + command.getCommandType());
//        Session conn = getConnection(command.getAuthString(), session);
        Session conn = session;
        if (conn != null) {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> join(conn, (JoinPlayer) command);
                case JOIN_OBSERVER -> observe(conn, (JoinObserver) command);
                case MAKE_MOVE -> move(conn, (MakeMove) command);
                case LEAVE -> leave(conn, (Leave) command);
                case RESIGN -> resign(conn, (Resign) command);
            }
        } else {
            sendError(session, "unknown user");
        }
    }

    public void join(Session conn, JoinPlayer command) throws IOException {
        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
        conn.getRemote().sendString(gson.toJson(serverMessage));
    }

    public void observe(Session conn, JoinObserver command) throws IOException {
        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
        conn.getRemote().sendString(gson.toJson(serverMessage));
    }

    public void move(Session conn, MakeMove command) throws IOException {
        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
        conn.getRemote().sendString(gson.toJson(serverMessage));
    }

    public void leave(Session conn, Leave command) throws IOException {
        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
        conn.getRemote().sendString(gson.toJson(serverMessage));
    }

    public void resign(Session conn, Resign command) throws IOException {
        Notification serverMessage = new Notification("Command received: " + command.getCommandType(), ServerMessage.ServerMessageType.NOTIFICATION);
        conn.getRemote().sendString(gson.toJson(serverMessage));
    }

    public void sendError(Session conn, String msg) throws IOException {
        ServerMessage message = new Error(msg, ServerMessage.ServerMessageType.ERROR);
        send(conn, message);
    }

    public void send(Session conn, ServerMessage msg) throws IOException {
        String json = gson.toJson(msg);
        conn.getRemote().sendString(json);
    }

    @OnWebSocketError
    public void onError(java.lang.Throwable throwable) {
        System.out.println("Websocket error!");
        System.out.println(throwable.toString());
    }

}