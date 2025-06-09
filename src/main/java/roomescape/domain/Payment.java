package roomescape.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.dto.PaymentRequest;
import roomescape.exception.ArgumentNullException;

import java.util.Objects;

@Entity
public class Payment {
    @Id
    private String paymentKey;

    private int amount;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public Payment(PaymentRequest request, Reservation reservation) {
        validateNull(request.paymentKey(), reservation);
        this.paymentKey = request.paymentKey();
        this.amount = request.amount();
        this.reservation = reservation;
    }

    protected Payment() {
    }

    private void validateNull(String paymentKey, Reservation reservation) {
        if (paymentKey == null || paymentKey.isEmpty()) {
            throw new ArgumentNullException("paymentKey");
        }
        if (reservation == null) {
            throw new ArgumentNullException("reservation");
        }
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public int getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Payment payment)) return false;
        return Objects.equals(paymentKey, payment.paymentKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paymentKey);
    }
}
