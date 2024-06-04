package roomescape.exception;

public class ExternalApiTimeoutException extends RuntimeException{

    public ExternalApiTimeoutException(String message) {
        super(message);
    }
}
