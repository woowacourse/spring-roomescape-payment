package roomescape.controller.handler.exception;

import roomescape.dto.response.ExceptionInfo;

public class PaymentException extends RuntimeException {

    private final ExceptionInfo exceptionInfo;
    public PaymentException(ExceptionInfo exceptionInfo) {
        super(exceptionInfo.message());
        this.exceptionInfo = exceptionInfo;
    }

    public ExceptionInfo getExceptionInfo() {
        return exceptionInfo;
    }
}
