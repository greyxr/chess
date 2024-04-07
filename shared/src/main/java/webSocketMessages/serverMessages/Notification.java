package webSocketMessages.serverMessages;

public class Notification extends ServerMessage {
    public String message;
    public Notification (String message, ServerMessageType type) {
        super(type);
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}
