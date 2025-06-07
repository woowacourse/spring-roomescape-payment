package roomescape.domain.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.domain.reservation.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.id = :reservationId
              AND r.status != 'CANCEL'
            """)
    Optional<Reservation> findByIdExcludingCanceled(Long reservationId);

    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM Reservation r
                WHERE r.time.id = :timeId
                  AND r.status != 'CANCEL'
            )
            """)
    boolean existsByTimeId(@Param("timeId") Long reservationTimeId);

    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM Reservation r
                WHERE r.date = :date
                  AND r.time.id = :timeId
                  AND r.theme.id = :themeId
                  AND r.status != 'CANCEL'
            )
            """)
    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.id = :id
              AND r.status != 'CANCEL'
            """)
    @EntityGraph(attributePaths = {"time", "theme"})
    Optional<Reservation> findByIdWithTimeAndTheme(Long id);

    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM Reservation r
                WHERE r.theme.id = :themeId
                  AND r.status != 'CANCEL'
            )
            """)
    boolean existsByThemeId(Long themeId);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.theme.id = :themeId
              AND r.date = :date
              AND r.status != 'CANCEL'
            """)
    List<Reservation> findByThemeIdAndDate(Long themeId, LocalDate date);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.status != 'CANCEL'
              AND (:themeId IS NULL OR r.theme.id = :themeId)
              AND (:memberId IS NULL OR r.member.id = :memberId)
              AND (:from IS NULL OR r.date >= :from)
              AND (:to IS NULL OR r.date <= :to)
            """)
    List<Reservation> findByThemeIdAndMemberIdAndDateBetween(Long themeId, Long memberId, LocalDate from, LocalDate to);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.member.id = :memberId
              AND r.status != 'CANCEL'
            """)
    @EntityGraph(attributePaths = {"time", "theme"})
    List<Reservation> findAllByMemberId(Long memberId);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.status != 'CANCEL'
            """)
    @EntityGraph(attributePaths = {"member", "time", "theme"})
    List<Reservation> findAllWithMemberAndTimeAndTheme();

    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM Reservation r
                WHERE r.date = :date
                  AND r.time.id = :timeId
                  AND r.theme.id = :themeId
                  AND r.member.id = :memberId
                  AND r.status != 'CANCEL'
            )
            """)
    boolean existsByDateAndTimeIdAndThemeIdAndMemberId(LocalDate date, Long timeId, Long themeId, Long memberId);
}
