package exceptions;

public class ClientException extends Exception {
    final private String message;

    public ClientException(String message) {
        super(message);
        this.message = message;
    }
}