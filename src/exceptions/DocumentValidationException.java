package exceptions;

import java.security.PrivilegedActionException;

/**
 * Exception thrown when a document fails validation.
 */
public class DocumentValidationException extends RuntimeException {
    /**
     * Constructs a new DocumentValidationException with the specified detail message.
     *
     * @param message the detail message explaining the validation failure
     */
    public DocumentValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public DocumentValidationException(Throwable cause) {
        super(cause);
    }
}
