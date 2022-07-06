package exceptions;

public class EmailListFromDatabaseException extends RuntimeException {
    public EmailListFromDatabaseException() {
    }

    public EmailListFromDatabaseException(String message) {
        super(message);
    }
}
