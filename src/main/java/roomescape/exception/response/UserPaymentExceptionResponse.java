package roomescape.exception.response;

import java.util.Arrays;

import roomescape.exception.type.UserPaymentExceptionType;

public class UserPaymentExceptionResponse {
    private final String errorCode;
    private final String message;

    private UserPaymentExceptionResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static UserPaymentExceptionResponse of(String errorCode, String message) {
        return Arrays.stream(UserPaymentExceptionType.values())
                .filter(userPaymentExceptionType -> userPaymentExceptionType.name().equals(errorCode))
                .findAny()
                .map(userPaymentExceptionType -> new UserPaymentExceptionResponse(
                        userPaymentExceptionType.name(),
                        userPaymentExceptionType.getMessage()))
                .orElse(new UserPaymentExceptionResponse(errorCode, message));
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
