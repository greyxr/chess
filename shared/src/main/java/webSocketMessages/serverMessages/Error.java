package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    public String errorMessage;
    public Error (String errorMessage, ServerMessageType type) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
