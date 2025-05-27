package roomescape.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.Member;
import roomescape.schedule.Schedule;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Schedule schedule;

    public Order(final String id, final Long amount, final PaymentStatus paymentStatus, final Member member, final Schedule schedule) {
        if (!schedule.isAmountEqualTo(amount)) {
            throw new IllegalArgumentException("주문 금액이 스케줄의 가격과 다릅니다.");
        }
        this.id = id;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.member = member;
        this.schedule = schedule;
    }

    public void pay(final Long amount, final Member member, final Schedule schedule) {
        if (!isAmount(amount)) {
            throw new IllegalArgumentException("주문 금액과 결제 금액이 일치하지 않아 결제를 할 수 없습니다.");
        }
        if (!isMember(member)) {
            throw new IllegalArgumentException("주문 회원과 결제 회원이 일치하지 않아 결제를 할 수 없습니다.");
        }
        if (!isSchedule(schedule)) {
            throw new IllegalArgumentException("주문 스케줄과 결제 스케줄이 일치하지 않아 결제를 할 수 없습니다.");
        }
        if (this.paymentStatus != PaymentStatus.WAITING) {
            throw new IllegalArgumentException("결제를 할 수 없는 상태입니다.");
        }
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    private boolean isAmount(final Long amount) {
        return Objects.equals(this.amount, amount);
    }

    private boolean isMember(final Member member) {
        return this.member.equals(member);
    }

    private boolean isSchedule(final Schedule schedule) {
        return this.schedule.equals(schedule);
    }
}
