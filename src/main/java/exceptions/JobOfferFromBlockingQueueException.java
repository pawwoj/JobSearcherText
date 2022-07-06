package exceptions;

public class JobOfferFromBlockingQueueException extends RuntimeException {
    public JobOfferFromBlockingQueueException() {
    }

    public JobOfferFromBlockingQueueException(String message) {
        super(message);
    }
}
