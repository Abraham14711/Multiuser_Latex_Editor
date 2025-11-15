package multilatex.backend.excpetions;

public class RefreshTokenTimeoutException extends RuntimeException {
    public RefreshTokenTimeoutException(String message) {
        super(message);
    }
}
