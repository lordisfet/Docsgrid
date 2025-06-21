package exceptions;

/**
 * Exception thrown when an illegal ID is provided (null or less than zero).
 */
public class IllegalIdException extends RuntimeException {
    /**
     * Constructs a new IllegalIdException with a default detail message.
     */
    public IllegalIdException() {
        super("ID cannot be null or less than 0.");
    }

    /**
     * Constructs a new IllegalIdException with the specified detail message.
     *
     * @param message the detail message explaining the validation failure
     */
    public IllegalIdException(String message) {
        super(message);
    }
}
