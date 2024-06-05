package roomescape.domain.reservationwaiting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.exception.reservationwaiting.NotFoundReservationWaitingException;

import java.util.List;
import java.util.Optional;

public interface ReservationWaitingRepository extends JpaRepository<ReservationWaiting, Long> {

    default ReservationWaiting getReservationWaitingById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundReservationWaitingException::new);
    }

    boolean existsByReservationAndMember(Reservation reservation, Member member);

    @Query("""
            SELECT new roomescape.domain.reservationwaiting.ReservationWaitingWithRank(
                w, COUNT(*))
            FROM ReservationWaiting w
            LEFT JOIN ReservationWaiting w2
                ON w2.id <= w.id
                AND w2.reservation.id = w.reservation.id
            WHERE w.member.id = :memberId
            GROUP BY w
            """)
    List<ReservationWaitingWithRank> findAllWaitingWithRankByMemberId(Long memberId);

    Optional<ReservationWaiting> findByReservationIdAndMemberId(Long reservationId, Long memberId);

    @Query("""
            SELECT w
            FROM ReservationWaiting w
            WHERE w.reservation = :reservation
            ORDER BY w.id
            LIMIT 1
            """)
    Optional<ReservationWaiting> findFirstByReservation(Reservation reservation);
}
