package roomescape.reservation.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    private static final String NON_EXISTS_ERROR_MESSAGE = "%s값이 존재하지 않습니다.";

    @EmbeddedId
    private PaymentId paymentId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(
            final PaymentId paymentId,
            final String paymentKey,
            final String orderId,
            final Long amount,
            final Reservation reservation
    ) {
        validateNotNull(paymentKey, orderId, amount, reservation);
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Payment(
            final String paymentKey,
            final String orderId,
            final Long amount,
            final Reservation reservation
    ) {
        this(null, paymentKey, orderId, amount, reservation);
    }

    private void validateNotNull(
            final String paymentKey,
            final String orderId,
            final Long amount,
            final Reservation reservation
    ) {
        if (paymentKey == null) {
            throw new IllegalArgumentException(NON_EXISTS_ERROR_MESSAGE.formatted("paymentKey"));
        }
        if (orderId == null) {
            throw new IllegalArgumentException(NON_EXISTS_ERROR_MESSAGE.formatted("orderId"));
        }
        if (amount == null) {
            throw new IllegalArgumentException(NON_EXISTS_ERROR_MESSAGE.formatted("amount"));
        }
        if (reservation == null) {
            throw new IllegalArgumentException(NON_EXISTS_ERROR_MESSAGE.formatted("reservation"));
        }
    }

    public PaymentId getPaymentId() {
        return paymentId;
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

    public Reservation getReservation() {
        return reservation;
    }
}
