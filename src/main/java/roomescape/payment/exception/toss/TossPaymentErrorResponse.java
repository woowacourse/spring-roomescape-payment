package roomescape.payment.exception.toss;

import roomescape.exception.ErrorType;
import roomescape.exception.ExceptionResponse;

public record TossPaymentErrorResponse(String code, String message) {

    public static TossPaymentErrorResponse from(ErrorType errorType) {
        return new TossPaymentErrorResponse(errorType.getErrorCode(), errorType.getMessage());
    }

    public ExceptionResponse toExceptionResponse() {
        return new ExceptionResponse(code, message);
    }
}
