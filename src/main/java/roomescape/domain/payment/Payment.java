package roomescape.domain.payment;

import jakarta.persistence.*;
import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private Long totalAmount;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(final String paymentKey, final String orderId, final Long totalAmount, final Reservation reservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
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

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
