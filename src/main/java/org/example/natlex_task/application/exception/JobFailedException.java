package org.example.natlex_task.application.exception;

public class JobFailedException extends RuntimeException {
    public JobFailedException() {
        super();
    }

    public JobFailedException(String message) {
        super(message);
    }

    public JobFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobFailedException(Throwable cause) {
        super(cause);
    }

}
