package roomescape.exception;

import roomescape.exception.response.UserPaymentExceptionResponse;

public class PaymentException extends RuntimeException {
    private final UserPaymentExceptionResponse userPaymentExceptionResponse;

    public PaymentException(UserPaymentExceptionResponse userPaymentExceptionResponse) {
        super(userPaymentExceptionResponse.getMessage());
        this.userPaymentExceptionResponse = userPaymentExceptionResponse;
    }

    public UserPaymentExceptionResponse getUserPaymentExceptionResponse() {
        return userPaymentExceptionResponse;
    }
}
