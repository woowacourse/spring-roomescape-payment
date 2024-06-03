package roomescape.domain;

import java.math.BigDecimal;

public class PaymentInfo {

    private final BigDecimal amount;
    private final String orderId;
    private final String paymentKey;

    public PaymentInfo(BigDecimal amount, String orderId, String paymentKey) {
        this.amount = amount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
