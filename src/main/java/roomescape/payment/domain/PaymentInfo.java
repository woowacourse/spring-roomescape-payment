package roomescape.payment.domain;


import lombok.Getter;

@Getter
public class PaymentInfo {
    private final String paymentKey;
    private final String orderId;
    private final long amount;

    public PaymentInfo(String paymentKey, String orderId, long amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
