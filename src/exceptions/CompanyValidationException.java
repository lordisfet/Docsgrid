package exceptions;

/**
 * Exception thrown when a Company entity fails validation.
 */
public class CompanyValidationException extends RuntimeException {
    /**
     * Constructs a new CompanyValidationException with the specified detail message.
     *
     * @param message the detail message explaining the validation failure
     */
    public CompanyValidationException(String message) {
        super(message);
    }
}