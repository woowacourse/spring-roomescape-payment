package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    default Reservation findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.RESERVATION_NOT_FOUND_BY_ID,
                "reservation_id = " + id
        ));
    }

    default int findReservationOrder(Reservation reservation) {
        return countByDateAndTimeIdAndThemeIdAndStatusAndIdLessThan(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                reservation.getStatus(),
                reservation.getId()
        ) + 1;
    }

    @EntityGraph(attributePaths = {"member", "theme", "time"})
    Optional<Reservation> findById(Long id);

    List<Reservation> findByMemberId(Long memberId);

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
            AND (r.status = :status)
            """)
    List<Reservation> findByMemberOrThemeOrDateRangeAndStatus(Long memberId, Long themeId, LocalDate startDate, LocalDate endDate, Status status);

    List<Reservation> findByStatusEquals(Status status);

    @Query("""
              SELECT r.theme.id
              FROM Reservation r
              WHERE r.date BETWEEN :startDate AND :endDate
              GROUP BY r.theme.id
              ORDER BY count(*) DESC
              LIMIT 10
            """)
    List<Long> findTopThemeIdsByReservationCountsForDate(LocalDate startDate, LocalDate endDate);

    int countByDateAndTimeIdAndThemeIdAndStatusAndIdLessThan(LocalDate date, Long timeId, Long themeId, Status status, Long id);

    boolean existsByTimeId(Long id);

    boolean existsByThemeId(Long id);

    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    boolean existsByDateAndTimeIdAndThemeIdAndStatus(LocalDate date, Long timeId, Long themeId, Status status);

    boolean existsByDateAndTimeIdAndThemeIdAndMemberId(LocalDate date, Long timeId, Long themeId, Long memberId);
}
