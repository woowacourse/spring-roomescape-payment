package roomescape.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.ReservationWaiting;
import roomescape.domain.reservation.ReservationWaitingWithRank;
import roomescape.domain.reservation.theme.Theme;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationWaitingRepository extends JpaRepository<ReservationWaiting, Long> {

    boolean existsByMemberAndDateAndTimeAndTheme(Member member, ReservationDate date, ReservationTime time, Theme theme);

    boolean existsByDateAndTimeAndTheme(ReservationDate date, ReservationTime time, Theme theme);

    @Query("""
            SELECT new roomescape.domain.ReservationWaitingWithRank(
                w,
                CAST((SELECT COUNT(w2) + 1
                    FROM ReservationWaiting w2
                    WHERE w2.theme = w.theme
                      AND w2.date = w.date
                      AND w2.time = w.time
                      AND w2.deniedAt IS NULL
                      AND w2.id < w.id) AS Long))
            FROM ReservationWaiting w
            WHERE w.member.id = :memberId
            """)
    List<ReservationWaitingWithRank> findAllWaitingWithRankByMemberId(Long memberId);

    Optional<ReservationWaiting> findTopByDateAndTimeIdAndThemeIdOrderById(ReservationDate date, Long timeId, Long themeId);
}
