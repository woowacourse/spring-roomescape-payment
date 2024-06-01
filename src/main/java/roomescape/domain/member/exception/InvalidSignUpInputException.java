package roomescape.domain.member.exception;

public class InvalidSignUpInputException extends RuntimeException {

    public InvalidSignUpInputException(final String message) {
        super(message);
    }
}
