package roomescape.infrastructure.payment.dto;

import lombok.Getter;

@Getter
public class PaymentApproveErrorResponse extends RuntimeException {

    private final String code;
    private final String message;

    public PaymentApproveErrorResponse(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
