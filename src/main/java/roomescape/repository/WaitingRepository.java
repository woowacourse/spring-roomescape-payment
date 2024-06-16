package roomescape.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.model.Member;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.Waiting;
import roomescape.model.WaitingWithRank;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends CrudRepository<Waiting, Long> {

    Waiting save(final Waiting waiting);

    void deleteById(final Long id);

    boolean existsById(final Long id);

    @Query("""
            SELECT new roomescape.model.WaitingWithRank(
            w, COUNT(w2))
            FROM Waiting w
            INNER JOIN Waiting w2
            ON w2.theme = w.theme
            AND w2.date = w.date
            AND w2.time = w.time
            AND w2.id <= w.id
            WHERE w.member.id = :memberId
            GROUP BY w.id
            """)
    List<WaitingWithRank> findWaitingWithRankByMemberId(final Long memberId);

    boolean existsWaitingByThemeAndDateAndTimeAndMember(final Theme theme, final LocalDate date, final ReservationTime time, final Member member);

    List<Waiting> findAll();

    boolean existsWaitingByThemeAndDateAndTime(final Theme theme, final LocalDate date, final ReservationTime time);

    Optional<Waiting> findFirstByThemeAndDateAndTime(final Theme theme, final LocalDate date, final ReservationTime time);
}
