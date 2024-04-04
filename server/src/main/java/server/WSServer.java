package server;

import com.google.gson.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import webSocketMessages.userCommands.*;
import webSocketMessages.userCommands.GameCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

@WebSocket
public class WSServer {

    Map<UUID, Session> sessions;

    public WSServer() {
        sessions = new HashMap<>();
    }
    public static void main(String[] args) {
//        Spark.port(8080);
//        Spark.webSocket("/connect", WSServer.class);
//        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    public Session getConnection(UUID authToken, Session session) {
        return sessions.get(authToken);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        CommandAdapter commandAdapter = new CommandAdapter();
        UserGameCommand command = commandAdapter.fromJson(msg);

        var conn = getConnection(command.authToken, session);
        if (conn != null) {
            switch (command.commandType) {
                case JOIN_PLAYER -> join(conn, msg);
                case JOIN_OBSERVER -> observe(conn, msg);
                case MAKE_MOVE -> move(conn, msg));
                case LEAVE -> leave(conn, msg);
                case RESIGN -> resign(conn, msg);
            }
        } else {
            Connection.sendError(session.getRemote(), "unknown user");
        }
    }

    @OnWebSocketError
    public void onError(java.lang.Throwable throwable) {
        System.out.println("Websocket error!");
        System.out.println(throwable.toString());
    }

}