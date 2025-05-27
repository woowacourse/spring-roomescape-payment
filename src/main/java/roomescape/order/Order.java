package roomescape.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.Member;
import roomescape.schedule.Schedule;

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
}
