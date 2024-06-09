package roomescape.domain.payment;

import jakarta.persistence.*;
import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    private String paymentKey;

    private String orderId;

    private Long amount;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    protected Payment() {
    }

    public Payment(final Reservation reservation, final String paymentKey,
                   final String orderId, final Long amount, final PaymentStatus status) {
        this(null, reservation, paymentKey, orderId, amount, status);
    }

    public Payment(final Reservation reservation, final PaymentStatus status) {
        this(null, reservation, null, null, null, status);
    }

    public Payment(final Long id, final Reservation reservation, final String paymentKey,
                   final String orderId, final Long amount, final PaymentStatus status) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public void toCanceled() {
        this.status = PaymentStatus.CANCELED;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
