package roomescape.domain.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.member.Member;
import roomescape.domain.reservationitem.ReservationItem;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_item_id", nullable = false)
    private ReservationItem reservationItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus reservationStatus;

    @Builder
    public Reservation(Member member, ReservationItem reservationItem, ReservationStatus reservationStatus) {
        this.member = member;
        this.reservationItem = reservationItem;
        this.reservationStatus = reservationStatus;
        reservationItem.addReservation(this);
    }

    public int priority() {
        return reservationItem.calculatePriorityOf(this);
    }

    public void denyAndChangeNextReservationToNotPaid() {
        Optional<Reservation> next = reservationItem.getNextReservationOf(this);
        if (next.isPresent()) {
            Reservation nextReservation = next.get();
            nextReservation.reservationStatus = ReservationStatus.NOT_PAID;
        }
        this.reservationStatus = ReservationStatus.DENIED;
    }

    public boolean isCreatedBy(Member member) {
        return this.member.getId().equals(member.getId());
    }
}
