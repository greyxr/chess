package exceptions;

public class BadRequestException extends Exception {
    final private int statusCode;
    final private String message;

    public BadRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int StatusCode() {
        return statusCode;
    }

    public String Message() {
        return message;
    }
}