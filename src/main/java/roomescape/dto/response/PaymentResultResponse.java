package roomescape.dto.response;

import roomescape.domain.PaymentResult;

public class PaymentResultResponse {

    private final String paymentKey;
    private final Long amount;

    public PaymentResultResponse(PaymentResult paymentResult) {
        if (paymentResult == null) {
            paymentKey = null;
            amount = null;
            return;
        }
        this.paymentKey = paymentResult.getPaymentKey();
        this.amount = paymentResult.getAmount();
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }
}
