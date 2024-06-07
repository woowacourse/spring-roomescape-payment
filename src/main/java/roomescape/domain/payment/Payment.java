package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.domain.reservation.Reservation;
import roomescape.service.payment.PaymentStatus;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private int amount;
    String orderName;
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public Payment() {
    }

    public Payment(String paymentKey, String orderId, String orderName, int amount, PaymentStatus status,
                   Reservation reservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.orderName = orderName;
        this.status = status;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
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

    public Reservation getReservation() {
        return reservation;
    }

    public String getOrderName() {
        return orderName;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
