package exceptions;

public class JsonNodeFromBlockingQueueException extends RuntimeException{
    public JsonNodeFromBlockingQueueException() {
    }

    public JsonNodeFromBlockingQueueException(String message) {
        super(message);
    }
}
