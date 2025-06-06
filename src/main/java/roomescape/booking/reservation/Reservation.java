package roomescape.booking.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.Member;
import roomescape.order.Order;
import roomescape.schedule.Schedule;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    private ReservationPaymentStatus paymentStatus;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    public Reservation(final Member member, final Schedule schedule, final Order order) {
        this.member = member;
        this.schedule = schedule;
        this.order = order;
        this.paymentStatus = ReservationPaymentStatus.SUCCESS;
    }

    public Reservation(final Member member,
                       final Schedule schedule,
                       final Order order,
                       final ReservationPaymentStatus paymentStatus) {
        this.member = member;
        this.schedule = schedule;
        this.order = order;
        this.paymentStatus = paymentStatus;
    }
}
