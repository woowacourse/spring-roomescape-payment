package roomescape.exception;

public class ExternalApiErrorException extends RuntimeException {

    public ExternalApiErrorException(String message) {
        super(message);
    }
}
