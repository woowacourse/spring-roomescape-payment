package roomescape.payment.domain;

import jakarta.persistence.CascadeType;
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
import java.util.Objects;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Schedule;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private PaymentKey paymentKey;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PAID;
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public Payment(String paymentKey, Member member, Schedule schedule) {
        this.id = null;
        this.paymentKey = new PaymentKey(paymentKey);
        this.member = Objects.requireNonNull(member);
        this.schedule = Objects.requireNonNull(schedule);
    }

    protected Payment() {
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey.paymentKey();
    }

    public boolean isPaid() {
        return status.isPaid();
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
