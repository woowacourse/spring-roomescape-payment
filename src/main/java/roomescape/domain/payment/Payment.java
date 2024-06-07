package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import roomescape.domain.BaseEntity;
import roomescape.domain.Member;
import roomescape.domain.Reservation;

@Entity
@SQLDelete(sql = "UPDATE payment SET deleted = TRUE WHERE payment.id = ?")
@SQLRestriction("deleted = FALSE ")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String paymentKey;
    private long amount;
    @OneToOne(fetch = FetchType.LAZY)
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

    public Payment(String orderId, String paymentKey, long amount, Reservation reservation) {
        this(null, orderId, paymentKey, amount, reservation);
    }

    public Payment(long id, Payment payment) {
        this(id, payment.orderId, payment.paymentKey, payment.amount, payment.reservation);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Member getMember() {
        return reservation.getReservationMember();
    }

    public long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
