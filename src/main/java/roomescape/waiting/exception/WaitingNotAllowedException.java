package roomescape.waiting.exception;

public class WaitingNotAllowedException extends RuntimeException {
    public WaitingNotAllowedException(String message) {
        super(message);
    }
}
