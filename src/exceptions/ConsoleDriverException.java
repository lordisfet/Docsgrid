package exceptions;

/**
 * Exception thrown when the ConsoleDriver contains some kind of error.
 */
public class ConsoleDriverException extends RuntimeException {
  public ConsoleDriverException(String message) {
    super(message);
  }
}
