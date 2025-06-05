package roomescape.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String paymentType;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public Payment() {
    }

    public Payment(String orderId, int amount, String paymentKey, String paymentType, Reservation reservation) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
