package roomescape.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.entity.Waiting;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.ReservationDate;
import roomescape.presentation.dto.response.WaitingWithRankResponse;

public interface WaitingRepository extends JpaRepository<Waiting, Id> {

    @Query("""
                SELECT new roomescape.presentation.dto.response.WaitingWithRankResponse(
                    w,
                    (
                        SELECT COUNT(w2) + 1L
                        FROM Waiting w2
                        WHERE w2.theme      = w.theme
                          AND w2.date       = w.date
                          AND w2.time       = w.time
                          AND w2.createdAt < w.createdAt
                    )
                )
                FROM Waiting w
                JOIN FETCH w.member
                JOIN FETCH w.time
                JOIN FETCH w.theme
                WHERE w.member.id = :userId
            """)
    List<WaitingWithRankResponse> findByUserIdWithRank(@Param("userId") Id userId);

    Optional<Waiting> findFirstByDateAndTimeAndThemeOrderById(ReservationDate date, TimeSlot time, Theme theme);
}
