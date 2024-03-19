package exceptions;

public class ServerError extends Exception {
    final private int statusCode;
    final private String message;

    public ServerError(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int statusCode() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}