package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private String paymentKey;
    private String orderId;
    private String type;
    private Integer totalAmount;
    private String status;
    private String requestedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;


    public Payment(final Reservation reservation, final String paymentKey, final String orderId, final String type,
                   final Integer totalAmount, final PaymentStatus paymentStatus) {
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.type = type;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    public Payment(final Reservation reservation, final PaymentStatus paymentStatus) {
        this.reservation = reservation;
        this.paymentStatus = paymentStatus;
    }


    public static Payment createPendingPayment(final Reservation reservation) {
        return new Payment(reservation, PaymentStatus.PENDING_FOR_PAYMENT);
    }

    public static Payment createRequestedPayment(final Reservation reservation, final String paymentKey,
                                                 final String orderId, final String type,
                                                 final Integer totalAmount) {
        return new Payment(reservation, paymentKey, orderId, type, totalAmount, PaymentStatus.PAYMENT_REQUESTED);

    }

    public void confirm(final String status, final String requestedAt) {
        this.status = status;
        this.requestedAt = requestedAt;
        this.paymentStatus = PaymentStatus.PAYMENT_CONFIRMED;
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

    public String getType() {
        return type;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public Payment() {
    }
}
