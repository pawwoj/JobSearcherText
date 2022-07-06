package exceptions;

public class JobOfferFromJsonNodeException extends RuntimeException{
    public JobOfferFromJsonNodeException() {
    }

    public JobOfferFromJsonNodeException(String message) {
        super(message);
    }
}
