package roomescape.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    private String orderId;

    private String paymentKey;

    private Integer amount;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(Long id,
                   String orderId,
                   String paymentKey,
                   Integer amount,
                   Reservation reservation) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Payment(String orderId,
                   String paymentKey,
                   Integer amount,
                   Reservation reservation) {
        this(null, orderId, paymentKey, amount, reservation);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Integer getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    protected void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
