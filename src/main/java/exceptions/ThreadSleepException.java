package exceptions;

public class ThreadSleepException extends RuntimeException {
    public ThreadSleepException() {
    }

    public ThreadSleepException(String message) {
        super(message);
    }
}
