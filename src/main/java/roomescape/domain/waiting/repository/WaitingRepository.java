package roomescape.domain.waiting.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.time.domain.ReservationTime;
import roomescape.domain.waiting.domain.Waiting;
import roomescape.domain.waiting.dto.WaitingWithRank;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @EntityGraph(attributePaths = {"time", "theme", "member", "payment"})
    List<Waiting> findAll();

    @Query(value = """
            SELECT
                w.id AS id,
                w.date AS date,
                t.name AS theme_name,
                rt.start_at AS start_at,
                m.name AS member_name,
                ROW_NUMBER() OVER (PARTITION BY :memberId ORDER BY w.created_at DESC) + 1 AS rank,
                p.payment_key AS payment_key,
                p.amount AS amount
            FROM waiting AS w
            LEFT JOIN theme AS t ON w.theme_id = t.id
            LEFT JOIN reservation_time AS rt ON w.time_id = rt.id
            LEFT JOIN member AS m ON w.member_id = m.id
            LEFT JOIN payment AS p ON w.payment_id = p.id
            WHERE w.member_id = :memberId
            """, nativeQuery = true)
    List<WaitingWithRank> findWithRankingByMember(@Param("memberId") long memberId);

    @EntityGraph(attributePaths = {"time", "theme", "member", "payment"})
    @Query("""
            SELECT w
            FROM Waiting w
            WHERE w.theme.id = :themeId
            AND w.date = :date
            AND w.time.id = :timeId
            ORDER BY w.createdAt
            LIMIT 1
            """)
    Optional<Waiting> findFirstWaiting(
            @Param("themeId") long themeId,
            @Param("date") LocalDate date,
            @Param("timeId") long timeId
    );

    @Query("""
            SELECT EXISTS(
                SELECT w
                FROM Waiting w
                WHERE w.theme.id = :themeId
                AND w.date = :date
                AND w.time.id = :timeId
                AND w.member.id = :memberId
            )
            """)
    boolean existsDuplicated(
            @Param("themeId") long themeId,
            @Param("date") LocalDate date,
            @Param("timeId") long timeId,
            @Param("memberId") long memberId
    );

    boolean existsByTheme(Theme theme);

    boolean existsByTime(ReservationTime reservationTime);
}
