package roomescape.reservation.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    List<Reservation> findAllByMember(Member member);

    List<Reservation> findAllByStatus(ReservationStatus status);

    Optional<Reservation> findFirstByReservationSlotOrderByCreatedAt(ReservationSlot reservationSlot);

    @Query("""
    SELECT count(*)
    FROM Reservation  r
    WHERE r.reservationSlot = 
    (
        SELECT r.reservationSlot
        FROM Reservation r
        WHERE r.id = :reservationId
    )
    AND r.createdAt < 
    (
        SELECT r.createdAt 
        FROM Reservation  r
        WHERE r.id = :reservationId
    )
    """)
    int findMyWaitingOrder(Long reservationId);

    boolean existsByReservationSlot(ReservationSlot reservationSlot);

    @Query("""
            SELECT count(*) > 0
            FROM Reservation r 
            WHERE r.reservationSlot.date = :date
            AND r.reservationSlot.theme.id = :themeId
            AND r.reservationSlot.time.id = :timeId
            """)
    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long themeId, Long timeId);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.reservationSlot.date = :date
            AND r.reservationSlot.theme.name = :theme
            AND r.reservationSlot.time.startAt = :time
            AND r.member.id = :memberId
            """)
    Reservation findByDateAndTimeAndThemeNameAndMemberId(LocalDate date, String theme, LocalTime time, Long memberId);

    void deleteByReservationSlot_Id(long reservationSlotId);

    boolean existsByReservationSlotAndMember(ReservationSlot reservationSlot, Member member);
}
