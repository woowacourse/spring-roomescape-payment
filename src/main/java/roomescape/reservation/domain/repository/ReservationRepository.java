package roomescape.reservation.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    List<Reservation> findAllByMember(Member member);

    List<Reservation> findAllByStatus(ReservationStatus status);

    Optional<Reservation> findFirstByReservationSlotOrderByCreatedAt(ReservationSlot reservationSlot);

    @Query("""
            SELECT COUNT(*)
            FROM Reservation r
            WHERE EXISTS (
                SELECT 1
                FROM Reservation r2
                WHERE r2.id = :reservationId
                  AND r.reservationSlot = r2.reservationSlot
                  AND r.createdAt < r2.createdAt
            )
            """)
    int findMyWaitingOrder(Long reservationId);

    boolean existsByReservationSlot(ReservationSlot reservationSlot);

    void deleteByReservationSlot_Id(long reservationSlotId);

    boolean existsByReservationSlotAndMember(ReservationSlot reservationSlot, Member member);
}
