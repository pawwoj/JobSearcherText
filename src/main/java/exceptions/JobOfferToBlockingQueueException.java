package exceptions;

public class JobOfferToBlockingQueueException extends RuntimeException {
    public JobOfferToBlockingQueueException() {
    }

    public JobOfferToBlockingQueueException(String message) {
        super(message);
    }
}
