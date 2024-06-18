package roomescape.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.repository.dto.MyReservationDto;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Boolean existsByTimeId(Long id);

    Boolean existsByThemeId(Long id);

    Boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    Boolean existsByMemberIdAndDateAndTimeIdAndThemeIdAndStatus(
        Long memberId, LocalDate date, Long timeId, Long themeId, ReservationStatus status);

    Optional<Reservation> findByIdAndStatus(Long id, ReservationStatus status);

    @EntityGraph(attributePaths = {"member", "theme", "time"}, type = EntityGraphType.FETCH)
    List<Reservation> findAllByDateAndThemeIdOrderByTimeStartAtAsc(LocalDate date, Long themeId);

    @EntityGraph(attributePaths = {"member", "theme", "time"}, type = EntityGraphType.FETCH)
    List<Reservation> findAllByThemeIdAndMemberIdAndDateIsBetweenOrderByDateAscTimeStartAtAsc(
        Long themeId, Long memberId, LocalDate dateFrom, LocalDate dateTo);

    @EntityGraph(attributePaths = {"member", "theme", "time"}, type = EntityGraphType.FETCH)
    List<Reservation> findAllByStatusOrderByDateAscTimeStartAtAsc(ReservationStatus status);

    long countByTimeAndThemeAndDateAndCreatedAtBefore(
        ReservationTime time, Theme theme, LocalDate date, LocalDateTime createdAt);

    @Query("""
        SELECT new roomescape.repository.dto.MyReservationDto(
            r, (
                SELECT COUNT(r2)
                FROM Reservation r2
                WHERE r2.theme = r.theme
                AND r2.date = r.date
                AND r2.time = r.time
                AND r2.createdAt < r.createdAt
            ), p
        )
        FROM Reservation r LEFT JOIN PaymentInfo p ON p.reservation.id = r.id
        WHERE r.member.id = :memberId
        ORDER BY
            r.date ASC,
            r.time.startAt ASC
        """)
    List<MyReservationDto> findReservationsWithRankByMemberId(Long memberId);
}
