package exceptions;

public class IllegalIdException extends RuntimeException {
    public IllegalIdException() {
        super("ID cannot be null or less than 0.");
    }

    public IllegalIdException(String message) {
        super(message);
    }
}

