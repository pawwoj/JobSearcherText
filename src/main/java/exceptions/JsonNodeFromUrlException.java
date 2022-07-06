package exceptions;

public class JsonNodeFromUrlException extends RuntimeException{
    public JsonNodeFromUrlException() {
    }

    public JsonNodeFromUrlException(String message) {
        super(message);
    }
}
