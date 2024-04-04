package server;

import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageObserver {
    public void notify(ServerMessage message);
}
