package roomescape.exception;

public record ExceptionResponse(String code, String message) {
    public static ExceptionResponse of(ErrorType errorType) {
        return new ExceptionResponse(errorType.getErrorCode(), errorType.getMessage());
    }
}
