package roomescape.exception;

import lombok.Getter;

public class PaymentApproveException extends RuntimeException{

    @Getter
    private final String code;
    private final String message;

    public PaymentApproveException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
