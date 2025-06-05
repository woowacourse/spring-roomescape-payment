package roomescape.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.exception.ArgumentNullException;

import java.util.Objects;

@Entity
public class Payment {
    @Id
    private String paymentKey;

    private int amount;

    private String orderId;

    @OneToOne
    private Reservation reservation;

    public Payment(PaymentInfo paymentInfo, Reservation reservation) {
        validateNull(paymentInfo.paymentKey(), paymentInfo.orderId(), reservation);
        this.paymentKey = paymentInfo.paymentKey();
        this.amount = paymentInfo.totalAmount();
        this.orderId = paymentInfo.orderId();
        this.reservation = reservation;
    }

    protected Payment() {
    }

    private void validateNull(String paymentKey, String orderId, Reservation reservation) {
        if (paymentKey == null || paymentKey.isEmpty()) {
            throw new ArgumentNullException("paymentKey");
        }
        if (orderId == null || orderId.isEmpty()) {
            throw new ArgumentNullException("orderId");
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

    public String getOrderId() {
        return orderId;
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
