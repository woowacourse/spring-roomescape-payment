package roomescape.dto.response;

import roomescape.domain.PaymentResult;

public class PaymentResultResponse {

    private final String paymentKey;
    private final long amount;

    public PaymentResultResponse(PaymentResult paymentResult) {
        this.paymentKey = paymentResult.getPaymentKey();
        this.amount = paymentResult.getAmount();
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public long getAmount() {
        return amount;
    }
}
