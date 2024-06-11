package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private int amount;
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public Payment() {
    }

    public Payment(String paymentKey, int amount, Reservation reservation) {
        this(null, paymentKey, amount, reservation);
    }

    public Payment(Long id, String paymentKey, int amount, Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
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
}
