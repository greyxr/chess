package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;

import webSocketMessages.*;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

@WebSocket
public class WSServer {
    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/connect", WSServer.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        GameCommand command = readJson(msg, UserGameCommand.class);

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

}