package roomescape.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import roomescape.member.model.Member;
import roomescape.reservation.model.Reservation;

@Entity
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long totalAmount;

    @OneToOne
    private Reservation reservation;

    @ManyToOne
    private Member member;

    protected PaymentHistory() {
    }

    public PaymentHistory(final String paymentKey,
                          final Long totalAmount,
                          final Reservation reservation,
                          final Member member) {
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.reservation = reservation;
        this.member = member;
    }

    public boolean hasSameReservation(final Reservation other) {
        return reservation.equals(other);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Member getMember() {
        return member;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
