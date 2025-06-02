package roomescape.booking.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.Member;
import roomescape.order.Order;
import roomescape.schedule.Schedule;

import static jakarta.persistence.FetchType.LAZY;

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
    private ReservationStatus reservationStatus;

    @OneToOne(fetch = LAZY)
    private Order order;

    public Reservation(final Member member, final Schedule schedule) {
        this.member = member;
        this.schedule = schedule;
        this.reservationStatus = ReservationStatus.CONFIRMED;
    }

    public Reservation(final Member member, final Schedule schedule, final ReservationStatus reservationStatus) {
        this.member = member;
        this.schedule = schedule;
        this.reservationStatus = reservationStatus;
    }

    public Reservation(final Member member, final Schedule schedule, final ReservationStatus reservationStatus, final Order order) {
        this.member = member;
        this.schedule = schedule;
        this.reservationStatus = reservationStatus;
        this.order = order;
    }

    public void markStatusAsConfirmed() {
        this.reservationStatus = ReservationStatus.CONFIRMED;
    }
}
