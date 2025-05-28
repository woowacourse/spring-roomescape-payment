package roomescape.booking.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.Member;
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

    public Reservation(final Member member, final Schedule schedule) {
        this.member = member;
        this.schedule = schedule;
        this.paymentStatus = ReservationPaymentStatus.SUCCESS;
    }

    public Reservation(final Member member, final Schedule schedule, final ReservationPaymentStatus paymentStatus) {
        this.member = member;
        this.schedule = schedule;
        this.paymentStatus = paymentStatus;
    }
}
