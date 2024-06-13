package roomescape.infrastructure;

import roomescape.domain.payment.PaymentProvider;
import roomescape.payment.PaymentResponse;

public class TossPaymentResponse implements PaymentResponse {

    private String paymentKey;
    private int totalAmount;

    public TossPaymentResponse(String paymentKey, int totalAmount) {
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
    }

    @Override
    public PaymentProvider getPaymentProvider() {
        return PaymentProvider.TOSS;
    }

    @Override
    public String getProviderPaymentId() {
        return paymentKey;
    }

    @Override
    public int getAmount() {
        return totalAmount;
    }
}
