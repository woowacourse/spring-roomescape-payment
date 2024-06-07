package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import roomescape.domain.member.Member;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private Long amount;

    private Boolean deleted;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    @ManyToOne
    private Member member;

    protected Payment() {
    }

    public Payment(String paymentKey, Long amount, Boolean deleted, LocalDateTime requestedAt, LocalDateTime approvedAt, Member member) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.deleted = deleted;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.member = member;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }
}
