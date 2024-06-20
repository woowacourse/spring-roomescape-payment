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

import java.time.ZonedDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private int amount;
    private String orderName;
    private ZonedDateTime requestedAt;
    private ZonedDateTime approvedAt;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, int amount, String orderName, ZonedDateTime requestedAt,
                   ZonedDateTime approvedAt, PaymentStatus status, Reservation reservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.orderName = orderName;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.status = status;
        this.reservation = reservation;
    }

    public void changeStatus(ZonedDateTime requestedAt, ZonedDateTime approvedAt, PaymentStatus status) {
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.status = status;
    }

    public boolean isCancelStatus() {
        return this.status == PaymentStatus.CANCELED;
    }

    public boolean isDoneStatus() {
        return this.status == PaymentStatus.DONE;
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

    public String getOrderName() {
        return orderName;
    }

    public ZonedDateTime getRequestedAt() {
        return requestedAt;
    }

    public ZonedDateTime getApprovedAt() {
        return approvedAt;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
