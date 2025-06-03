package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.reservation.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select r from Reservation r
            join fetch r.registrationSlot bs
            where (:themeId is null or bs.theme.id = :themeId)
              and (:memberId is null or r.member.id = :memberId)
              and (:localDateFrom is null or bs.date >= :localDateFrom)
              and (:localDateTo is null or bs.date <= :localDateTo)
            """)
    List<Reservation> findByCriteria(
            @Param("themeId") Long themeId,
            @Param("memberId") Long memberId,
            @Param("localDateFrom") LocalDate localDateFrom,
            @Param("localDateTo") LocalDate localDateTo
    );

    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM Reservation r
        WHERE r.registrationSlot.date = :date
          AND r.registrationSlot.time.id = :timeId
          AND r.registrationSlot.theme.id = :themeId
        )
    """)
    boolean existsSameSlot(LocalDate date, Long timeId, Long themeId);

    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM Reservation r
        WHERE r.member.id = :memberId
          AND r.registrationSlot.theme.id = :themeId
          AND r.registrationSlot.time.id = :timeId
          AND r.registrationSlot.date = :date
        )
    """)
    boolean memberHasReservationAtSlot(Long memberId, Long themeId, Long timeId, LocalDate date);
}
