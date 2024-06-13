package roomescape.system.exception.model;

import roomescape.system.exception.error.ErrorType;

public class PaymentException extends CustomException {
    private int statusCode;

    public PaymentException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

    public PaymentException(final ErrorType errorType, final String message, final int statusCode) {
        this(errorType, message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
