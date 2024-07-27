package org.example.natlex_task.application.exception;

public class JobInProgressException extends RuntimeException {
    public JobInProgressException() {
        super();
    }

    public JobInProgressException(String message) {
        super(message);
    }

    public JobInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobInProgressException(Throwable cause) {
        super(cause);
    }

}
