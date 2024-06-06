package roomescape.domain.reservationwaiting;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationWaitingRepository extends JpaRepository<ReservationWaiting, Long> {
    @Query("""
            SELECT new roomescape.domain.reservationwaiting.ReservationWaitingWithRank(
                w,
                COUNT(previous)
            )
            FROM ReservationWaiting w
            LEFT JOIN ReservationWaiting previous
                ON previous.theme = w.theme
                AND previous.time = w.time
                AND previous.date = w.date
                AND previous.id < w.id
            AND w.member.id = :memberId
            GROUP BY w
            ORDER BY w.id
            """)
    List<ReservationWaitingWithRank> findReservationWaitWithRankByMemberId(Long memberId);

    boolean existsByDateAndTimeIdAndThemeIdAndMemberId(LocalDate date, long timeId, long themeId, long memberId);

    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, long timeId, long themeId);

    Optional<ReservationWaiting> findFirstByDateAndThemeIdAndTimeIdOrderById(LocalDate date, long themeId, long timeId);
}
