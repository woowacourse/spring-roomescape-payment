package roomescape.mvc.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.reservation.domain.Reservation;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.time.domain.ReservationTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"reservationTime", "theme", "member", "payment"})
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"reservationTime", "theme", "member", "payment"})
    List<Reservation> findByMember(Member member);

    boolean existsByReservationTime(ReservationTime reservationTime);

    boolean existsByTheme(Theme theme);

    boolean existsByThemeAndDateAndReservationTime(Theme theme, LocalDate date, ReservationTime reservationTime);

    @Query(value =
            """
                    SELECT r
                    FROM Reservation AS r
                    JOIN FETCH r.reservationTime AS rt
                    JOIN FETCH r.theme AS t
                    JOIN FETCH r.member AS m
                    WHERE m.id = :memberId
                    AND t.id = :themeId
                    AND r.date BETWEEN :fromDate AND :toDate
                    ORDER BY r.id
                    """)
    List<Reservation> findReservationsByFilter(
            @Param("memberId") Long memberId,
            @Param("themeId") Long themeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
