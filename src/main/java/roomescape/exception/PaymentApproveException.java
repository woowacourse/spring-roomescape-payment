package roomescape.exception;

import lombok.Getter;

@Getter
public class PaymentApproveException extends RuntimeException {

    private final String code;
    private final String message;

    public PaymentApproveException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
