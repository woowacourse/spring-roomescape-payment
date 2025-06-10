package roomescape.payment.domain;

import jakarta.persistence.*;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    Reservation reservation;

    String paymentKey;

    String orderId;

    int amount;

    protected Payment() {
    }

    private Payment(Long id, Reservation reservation, String paymentKey, String orderId, int amount) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public static Payment createWithoutId(Reservation reservation, String paymentKey, String orderId, int amount) {
        return new Payment(null, reservation, paymentKey, orderId, amount);
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

    public int getAmount() {
        return amount;
    }
}
