package roomescape.reservation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.dto.MemberRegistrationProjection;

public interface RegistrationQueryRepository extends JpaRepository<Reservation, Long> {

    //TODO: 하드코딩 문제를 어떻게 해결할 수 있을까, 식별자(0,1)로 구분해야할까?
    @Query(value = """
        SELECT
            r.id AS id,
            'BOOKED' AS type,
            t.name AS themeName,
            r.date AS date,
            rt.start_at AS time,
            0 AS rank,
            p.payment_key AS paymentKey,
            p.amount AS amount
        FROM reservation r
        JOIN theme t ON r.theme_id = t.id
        JOIN reservation_time rt ON r.time_id = rt.id
        LEFT JOIN payment p ON p.reservation_id = r.id
        WHERE r.member_id = :memberId

        UNION ALL

        SELECT
            w.id AS id,
            'WAITING' AS type,
            t.name AS themeName,
            w.date AS date,
            rt.start_at AS time,
            
            (
                SELECT COUNT(*) FROM waiting_reservation w2
                WHERE w2.date = w.date
                  AND w2.time_id = w.time_id
                  AND w2.theme_id = w.theme_id
                  AND (
                        w2.created_at < w.created_at OR
                        (w2.created_at = w.created_at AND w2.id < w.id)
                      )
            ) + 1 AS rank,
            '' AS paymentKey,
            0 AS amount
        FROM waiting_reservation w
        JOIN theme t ON w.theme_id = t.id
        JOIN reservation_time rt ON w.time_id = rt.id
        WHERE w.member_id = :memberId

        ORDER BY date, time
        """, nativeQuery = true)
    List<MemberRegistrationProjection> findAllRegistrationsByMemberId(@Param("memberId") Long memberId);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM reservation r
        WHERE r.time_id = :timeId
        
        UNION
        
        SELECT 1 FROM waiting_reservation wr
        WHERE wr.time_id = :timeId
      )
    """, nativeQuery = true)
    boolean existsByTimeId(Long timeId);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM reservation r
        WHERE r.theme_id = :themeId
        
        UNION
        
        SELECT 1 FROM waiting_reservation wr
        WHERE wr.theme_id = :themeId
      )
    """, nativeQuery = true)
    boolean existsByThemeId(Long themeId);
}
