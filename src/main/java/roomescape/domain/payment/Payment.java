package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import roomescape.domain.Member;
import roomescape.domain.Reservation;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String paymentKey;
    private long amount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String orderId, String paymentKey, long amount) {
        this(null, orderId, paymentKey, amount, null);
    }

    public Payment(Long id, String orderId, String paymentKey, long amount, Reservation reservation) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Payment(long id, Payment payment) {
        this(id, payment.orderId, payment.paymentKey, payment.amount, payment.reservation);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return reservation.getReservationMember();
    }

    public Reservation getReservation() {
        return reservation;
    }
}
