package roomescape.global.exception;

public class ExternalApiException extends BusinessException {

    public ExternalApiException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
