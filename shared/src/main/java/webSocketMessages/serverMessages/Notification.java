package webSocketMessages.serverMessages;

public class Notification extends ServerMessage {
    String message;
    public Notification (String message, ServerMessageType type) {
        super(type);
        this.message = message;
    }
}
