package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.domain.reservation.Reservation;

import java.math.BigDecimal;

@Entity
public class Payment {

    private static final int MAX_PAYMENT_KEY_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_PAYMENT_KEY_LENGTH)
    private String paymentKey;

    @Embedded
    private Amount amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String paymentKey, BigDecimal amount, Reservation reservation) {
        validatePaymentKey(paymentKey);
        validateReservation(reservation);

        this.paymentKey = paymentKey;
        this.amount = new Amount(amount);
        this.reservation = reservation;
    }

    private void validatePaymentKey(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key는 필수입니다.");
        }
        if (paymentKey.length() > MAX_PAYMENT_KEY_LENGTH) {
            throw new IllegalArgumentException(String.format("Payment key는 최대 %d자입니다.", MAX_PAYMENT_KEY_LENGTH));
        }
    }

    private void validateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation은 필수입니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount.getValue();
    }

    public Long getReservationId() {
        return reservation.getId();
    }
}
