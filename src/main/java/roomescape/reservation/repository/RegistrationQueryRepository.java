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
                ri.date AS date,
                rt.start_at AS time,
                0 AS rank,
                p.payment_key AS paymentKey,
                p.amount AS amount
            FROM reservation r
            JOIN room_escape_information ri ON r.room_escape_information_id = ri.id
            JOIN theme t ON ri.theme_id = t.id
            JOIN reservation_time rt ON ri.time_id = rt.id
            JOIN payment p ON p.reservation_id = r.id
            WHERE r.member_id = :memberId
            
            UNION ALL
            
            SELECT
                w.id AS id,
                'WAITING' AS type,
                t.name AS themeName,
                ri.date AS date,
                rt.start_at AS time,
                (
                    SELECT COUNT(*) FROM waiting_reservation w2
                    JOIN room_escape_information ri2 ON w2.room_escape_information_id = ri2.id
                    WHERE ri2.date = ri.date
                      AND ri2.time_id = ri.time_id
                      AND ri2.theme_id = ri.theme_id
                      AND (
                            w2.created_at < w.created_at OR
                            (w2.created_at = w.created_at AND w2.id < w.id)
                          )
                ) + 1 AS rank,
                '' AS paymentKey,
                0 AS amount
            FROM waiting_reservation w
            JOIN room_escape_information ri ON w.room_escape_information_id = ri.id
            JOIN theme t ON ri.theme_id = t.id
            JOIN reservation_time rt ON ri.time_id = rt.id
            WHERE w.member_id = :memberId
            
            ORDER BY date, time
        """, nativeQuery = true)
    List<MemberRegistrationProjection> findAllRegistrationsByMemberId(@Param("memberId") Long memberId);
}
