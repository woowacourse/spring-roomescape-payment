package roomescape.exception;

public class BadRequestException extends RuntimeException {

    private static final String ERROR = "[ERROR] ";

    public BadRequestException(final String message) {
        super(message);
    }

    public BadRequestException(final String value, final String field) {
        super(ERROR + "%s의 값이 \"%s\"일 수 없습니다.".formatted(field, value));
    }
}
