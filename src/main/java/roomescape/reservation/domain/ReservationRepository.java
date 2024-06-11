package roomescape.reservation.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            SELECT r
            FROM Reservation AS r
            WHERE r.member.id = :memberId
                AND r.theme.id = :themeId
                AND r.date.value BETWEEN :dateFrom AND :dateTo
            """)
    List<Reservation> findByMemberAndThemeAndPeriod(@Param("memberId") Long memberId,
                                                    @Param("themeId") Long themeId,
                                                    @Param("dateFrom") LocalDate dateFrom,
                                                    @Param("dateTo") LocalDate dateTo);

    @Query("""
            SELECT new roomescape.reservation.domain.MyReservation(r1, COUNT(*), p.paymentKey,p.orderId,p.amount)
            FROM Reservation AS r1
            INNER JOIN Reservation AS r2 
                ON r1.date = r2.date
                AND r1.time = r2.time
                AND r1.theme = r2.theme
            INNER JOIN Payment AS p
                ON p.reservation.id = r1.id
            JOIN FETCH r1.theme
            JOIN FETCH r1.time
            JOIN FETCH r1.member
            WHERE r1.member.id = :memberId
                AND r1.id >= r2.id
            GROUP BY r1
            """)
    List<MyReservation> findMyReservations(@Param("memberId") Long memberId);

    @Query("""
            SELECT r
            FROM Reservation AS r
            JOIN FETCH r.time
            JOIN FETCH r.theme
            JOIN FETCH r.member
            WHERE r.date.value =:date
                AND r.time.id =:timeId
                AND r.theme.id =:themeId
            """)
    List<Reservation> findByDateAndTimeAndTheme(@Param("date") LocalDate date,
                                                @Param("timeId") Long timeId,
                                                @Param("themeId") Long themeId);

    @Query("""
            SELECT r1
            FROM Reservation AS r1
            INNER JOIN Reservation AS r2
                ON r1.time = r2.time
                AND r1.date = r2.date
                AND r1.theme = r2.theme
            JOIN FETCH r1.member
            JOIN FETCH r1.theme
            JOIN FETCH r1.time
            WHERE r1.id > r2.id
            """)
    List<Reservation> findReservationOnWaiting();
}
