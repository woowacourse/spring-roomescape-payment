package roomescape.exception;

import java.util.HashMap;
import java.util.Map;

public class MethodArgumentNotValidExceptionResponse {
    private final String errorCode;

    private final String message;

    public MethodArgumentNotValidExceptionResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    private Map<String, String> validation = new HashMap<>();

    public void addValidation(String fieldName, String errorMessage) {
        this.validation.put(fieldName, errorMessage);
    }

    public static MethodArgumentNotValidExceptionResponse of(ErrorType errorType) {
        return new MethodArgumentNotValidExceptionResponse(errorType.getErrorCode(), errorType.getMessage());
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getValidation() {
        return validation;
    }
}
