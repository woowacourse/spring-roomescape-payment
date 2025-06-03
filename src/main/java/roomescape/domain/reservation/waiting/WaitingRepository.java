package roomescape.domain.reservation.waiting;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @EntityGraph(attributePaths = {"user", "theme", "timeSlot"})
    Optional<Waiting> findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(LocalDate date, long timeSlotId, long themeId);

    @Query("""
                SELECT new roomescape.domain.reservation.waiting.WaitingWithRank(
                    w,
                    (
                        SELECT COUNT(w2)
                        FROM Waiting w2
                        WHERE w2.theme = w.theme
                          AND w2.date = w.date
                          AND w2.timeSlot = w.timeSlot
                          AND w2.id < w.id
                    )
                )
                FROM Waiting w
                JOIN FETCH w.user
                JOIN FETCH w.theme
                JOIN FETCH w.timeSlot
                WHERE w.user.id = :userId
            """)
    List<WaitingWithRank> findWaitingWithRankByUserId(Long userId);

    boolean existsByDateAndTimeSlotIdAndThemeIdAndUserId(LocalDate date, long timeId, long themeId, long userId);
}
