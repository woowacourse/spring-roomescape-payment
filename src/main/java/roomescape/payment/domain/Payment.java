package roomescape.payment.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.exception.BadArgumentRequestException;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Schedule;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private PaymentKey paymentKey;
    @Column(nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public Payment(String paymentKey, BigDecimal amount, Member member, Schedule schedule) {
        this.id = null;
        this.amount = Objects.requireNonNull(amount);
        this.paymentKey = new PaymentKey(paymentKey);
        this.status = PaymentStatus.PAID;
        this.member = Objects.requireNonNull(member);
        this.schedule = Objects.requireNonNull(schedule);
    }

    protected Payment() {
    }

    public boolean isPaid() {
        return status.isPaid();
    }

    public void completeRefund() {
        if (status.isPaid()) {
            status = PaymentStatus.REFUND;
            return;
        }
        throw new BadArgumentRequestException("이미 환불된 결제입니다.");
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey.paymentKey();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Member getMember() {
        return member;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Payment payment = (Payment) object;
        return Objects.equals(id, payment.id) && Objects.equals(paymentKey, payment.paymentKey)
                && Objects.equals(member, payment.member) && Objects.equals(schedule, payment.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentKey, member, schedule);
    }
}
