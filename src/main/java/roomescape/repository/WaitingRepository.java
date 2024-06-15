package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.Member;
import roomescape.domain.Theme;
import roomescape.domain.TimeSlot;
import roomescape.domain.Waiting;
import roomescape.domain.WaitingWithRank;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @Query("""
            SELECT new roomescape.domain.WaitingWithRank(
            w,
            (SELECT COUNT(w2) + 1
                FROM Waiting w2
                WHERE w2.theme = w.theme
                AND w2.date = w.date
                AND w2.time = w.time
                AND w2.id < w.id))
            FROM Waiting w
            WHERE w.member = :member
            ORDER BY w.date, w.time.startAt
            """)
    List<WaitingWithRank> findWaitingsWithRankByMemberIdByDateAsc(Member member);

    Optional<Waiting> findFirstByDateAndTimeAndTheme(LocalDate date, TimeSlot timeSlot, Theme theme);

    boolean existsByDateAndTimeAndThemeAndMember(LocalDate date, TimeSlot timeSlot, Theme theme, Member member);

    boolean existsByDateAndTimeAndTheme(LocalDate date, TimeSlot timeSlot, Theme theme);
}
