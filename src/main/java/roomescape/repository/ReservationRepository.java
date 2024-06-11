package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"member", "theme", "time"})
    Optional<Reservation> findById(Long id);

    @EntityGraph(attributePaths = {"member", "theme", "time"})
    List<Reservation> findAll();

    List<Reservation> findByMemberIdAndStatusIsNot(Long memberId, Status status);

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.member
            JOIN FETCH r.time
            JOIN FETCH r.theme
            WHERE (:memberId IS NULL OR r.member.id = :memberId)
            AND (:themeId IS NULL OR r.theme.id = :themeId)
            AND (:startDate IS NULL OR r.date >= :startDate)
            AND (:endDate IS NULL OR r.date <= :endDate)
            """)
    List<Reservation> findByMemberOrThemeOrDateRange(Long memberId, Long themeId, LocalDate startDate,
                                                     LocalDate endDate);

    List<Reservation> findByDateAndTimeIdAndThemeIdAndStatus(LocalDate date, Long timeId, Long themeId, Status status);

    @Query("""
              SELECT r.theme.id
              FROM Reservation r
              WHERE r.date BETWEEN :startDate AND :endDate
              GROUP BY r.theme.id
              ORDER BY count(*) DESC
              LIMIT 10
            """)
    List<Long> findTopThemeIdsByReservationCountsForDate(LocalDate startDate, LocalDate endDate);

    int countByDateAndTimeIdAndThemeIdAndStatus(LocalDate date, Long timeId, Long themeId, Status status);

    boolean existsByTimeId(Long id);

    boolean existsByThemeId(Long id);

    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    boolean existsByDateAndTimeIdAndThemeIdAndMemberId(LocalDate date, Long timeId, Long themeId, Long memberId);
}
