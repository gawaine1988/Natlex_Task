package org.example.natlex_task.adapter.exception;

public class ArgumentNotValidException extends RuntimeException {
    // Constructor with no arguments
    public ArgumentNotValidException() {
        super();
    }

    // Constructor that accepts a message
    public ArgumentNotValidException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public ArgumentNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public ArgumentNotValidException(Throwable cause) {
        super(cause);
    }
}
