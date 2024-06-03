package roomescape.exception.response;

import java.util.Arrays;

import org.springframework.http.HttpStatusCode;

import roomescape.exception.type.PaymentExceptionType;

public class PaymentExceptionResponse {
    private final HttpStatusCode httpStatusCode;
    private final String errorCode;
    private final String message;

    public PaymentExceptionResponse(HttpStatusCode httpStatusCode, String errorCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static PaymentExceptionResponse of(HttpStatusCode httpStatusCode, String errorCode, String message) {
        return Arrays.stream(PaymentExceptionType.values())
                .filter(paymentExceptionType -> paymentExceptionType.name().equals(errorCode))
                .findAny()
                .map(paymentExceptionType -> new PaymentExceptionResponse(
                        paymentExceptionType.getHttpStatus(),
                        paymentExceptionType.name(),
                        paymentExceptionType.getMessage()))
                .orElse(new PaymentExceptionResponse(httpStatusCode, errorCode, message));
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
