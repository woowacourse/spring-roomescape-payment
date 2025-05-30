package roomescape.payment.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String paymentKey;

    private LocalDateTime paymentDateTime;

    private Long amount;

    private PaymentStatus status;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Reservation reservation;

    public Payment(String orderId, String paymentKey, LocalDateTime paymentDateTime, Long amount, PaymentStatus status, Reservation reservation) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentDateTime = paymentDateTime;
        this.amount = amount;
        this.status = status;
        this.reservation = reservation;
    }

    public Payment() {
    }

    public void cancel() {
        this.status = PaymentStatus.CANCEL;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public Long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
