package roomescape.reservation.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    protected TossPayment() {
    }

    public TossPayment(
            final OrderId orderId,
            final Amount amount,
            final PaymentKey paymentKey,
            final Reservation reservation
    ) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.reservation = reservation;
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
