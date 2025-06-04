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

    @Override
    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.id = :reservationId
              AND r.status != 'CANCEL'
            """)
    Optional<Reservation> findById(@Param("reservationId") Long reservationId);

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
    boolean existsByDateAndTimeIdAndThemeId(@Param("date") LocalDate reservationDate,
                                            @Param("timeId") Long timeId,
                                            @Param("themeId") Long themeId);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.id = :id
              AND r.status != 'CANCEL'
            """)
    @EntityGraph(attributePaths = {"time", "theme"})
    Optional<Reservation> findByIdWithTimeAndTheme(@Param("id") Long id);

    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM Reservation r
                WHERE r.theme.id = :themeId
                  AND r.status != 'CANCEL'
            )
            """)
    boolean existsByThemeId(@Param("themeId") Long themeId);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.theme.id = :themeId
              AND r.date = :date
              AND r.status != 'CANCEL'
            """)
    List<Reservation> findByThemeIdAndDate(@Param("themeId") Long themeId, @Param("date") LocalDate reservationDate);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.status != 'CANCEL'
              AND (:themeId IS NULL OR r.theme.id = :themeId)
              AND (:memberId IS NULL OR r.member.id = :memberId)
              AND (:from IS NULL OR r.date >= :from)
              AND (:to IS NULL OR r.date <= :to)
            """)
    List<Reservation> findByThemeIdAndMemberIdAndDateBetween(@Param("themeId") Long themeId,
                                                             @Param("memberId") Long memberId,
                                                             @Param("from") LocalDate from,
                                                             @Param("to") LocalDate to);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.member.id = :memberId
              AND r.status != 'CANCEL'
            """)
    @EntityGraph(attributePaths = {"time", "theme"})
    List<Reservation> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT r
            FROM Reservation r
                JOIN FETCH r.member m
                JOIN FETCH r.time t
                JOIN FETCH r.theme th
            WHERE r.status != 'CANCEL'
            """)
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
    boolean existsByDateAndTimeIdAndThemeIdAndMemberId(@Param("date") LocalDate date,
                                                       @Param("timeId") Long timeId,
                                                       @Param("themeId") Long themeId,
                                                       @Param("memberId") Long memberId);
}
