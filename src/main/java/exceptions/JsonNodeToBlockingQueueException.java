package exceptions;

public class JsonNodeToBlockingQueueException extends RuntimeException{
    public JsonNodeToBlockingQueueException() {
    }

    public JsonNodeToBlockingQueueException(String message) {
        super(message);
    }
}
