package exceptions;

public class CompanyValidationException extends RuntimeException {
    public CompanyValidationException(String message) {
        super(message);
    }
}
