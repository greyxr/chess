package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    String notification;
    public Error (String notification, ServerMessageType type) {
        super(type);
        this.notification = notification;
    }
}
