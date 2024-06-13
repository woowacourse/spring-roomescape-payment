package roomescape.domain.reservationwaiting;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;

public interface ReservationWaitingRepository extends JpaRepository<ReservationWaiting, Long> {
    boolean existsByReservationAndMember(Reservation reservation, Member member);

    Optional<ReservationWaiting> findByReservationIdAndMemberId(Long reservationId, Long memberId);

    @Query("""
            SELECT w
            FROM ReservationWaiting w
            WHERE w.reservation = :reservation
            ORDER BY w.id
            LIMIT 1
            """)
    Optional<ReservationWaiting> findFirstByReservation(Reservation reservation);

    List<ReservationWaiting> findAllWaitingByMemberId(Long memberId);

    long countByReservationIdAndIdLessThan(Long reservationId, Long id);
}
