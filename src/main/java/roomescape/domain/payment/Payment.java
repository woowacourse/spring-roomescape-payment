package roomescape.domain.payment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SoftDelete;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private Long amount;
    @ManyToOne
    private Member member;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Reservation reservation;

    public Payment() {
    }

    public Payment(String paymentKey, Long amount) {
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }
}
