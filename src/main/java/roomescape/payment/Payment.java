package roomescape.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.payment.domain.PaymentType;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private int amount;

    @Enumerated
    private PaymentType paymentType;

    protected Payment () {
    }

    public Payment(String paymentKey, String orderId, int amount, PaymentType paymentType) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
}
