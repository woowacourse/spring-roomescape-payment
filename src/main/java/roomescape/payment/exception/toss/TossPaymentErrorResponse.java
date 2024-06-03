package roomescape.payment.exception.toss;

import roomescape.exception.ErrorType;

public record TossPaymentErrorResponse(String code, String message) {

    public static TossPaymentErrorResponse from(ErrorType errorType) {
        return new TossPaymentErrorResponse(errorType.getErrorCode(), errorType.getMessage());
    }
}
