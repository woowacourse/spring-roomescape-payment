package roomescape.reservation.waiting.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.MemberId;
import roomescape.reservation.waiting.domain.Waiting;
import roomescape.reservation.waiting.domain.WaitingId;
import roomescape.reservation.waiting.dto.response.WaitingWithRank;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeId;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeId;

public interface WaitingRepository extends JpaRepository<Waiting, WaitingId> {

    @Query("""
            SELECT w2
            FROM Waiting w2
            JOIN FETCH w2.theme
            JOIN FETCH w2.member
            JOIN FETCH w2.time
            """)
    List<Waiting> findAll();

    boolean existsByDateAndTimeIdAndThemeId(
            LocalDate date,
            ReservationTimeId timeId,
            ThemeId themeId
    );

    boolean existsByDateAndTimeIdAndThemeIdAndMemberId(
            LocalDate date,
            ReservationTimeId timeId,
            ThemeId themeId,
            MemberId memberId
    );

    @Query("""
            SELECT new roomescape.reservation.waiting.domain.WaitingWithRank(
                w,
                (SELECT COUNT(w2)
                 FROM Waiting w2
                 WHERE w2.theme = w.theme
                   AND w2.date = w.date
                   AND w2.time = w.time
                   AND w2.createdAt < w.createdAt))
            FROM Waiting w
            WHERE w.member.id = :memberId
            """)
    List<WaitingWithRank> findAllWaitingWithRankByMemberId(MemberId memberId);

    Optional<Waiting> findFirstByDateAndTimeAndThemeOrderByCreatedAtAsc(
            LocalDate date,
            ReservationTime time,
            Theme theme
    );
}
