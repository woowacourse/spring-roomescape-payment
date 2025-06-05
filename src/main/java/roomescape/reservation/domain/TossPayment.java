package roomescape.reservation.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "toss_payment")
public class TossPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OrderId orderId;

    @Embedded
    private Amount amount;

    @Embedded
    private PaymentKey paymentKey;

    protected TossPayment() {
    }

    public TossPayment(
            final OrderId orderId,
            final Amount amount,
            final PaymentKey paymentKey
    ) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
    }

    public Long getId() {
        return id;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Amount getAmount() {
        return amount;
    }

    public PaymentKey getPaymentKey() {
        return paymentKey;
    }
}
