package roomescape.infrastructure.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.WaitingRankReservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Query(value = """
            SELECT r
            FROM Reservation r
                JOIN FETCH r.theme
                JOIN FETCH r.member
                JOIN FETCH r.reservationTime
            """)
    List<Reservation> findAll();

    @Query(value = """
            SELECT r
            FROM Reservation r
                JOIN FETCH r.reservationTime
            WHERE r.theme = :theme AND r.date = :date
            """)
    List<Reservation> findAllByThemeAndDate(Theme theme, ReservationDate date);

    @Query(value = """
            SELECT r
            FROM Reservation r
                JOIN FETCH r.theme
                JOIN FETCH r.member
                JOIN FETCH r.reservationTime
            WHERE r.member = :member AND r.theme = :theme AND r.date BETWEEN :from AND :to
            """)
    List<Reservation> findAllByMemberAndThemeAndDateBetween(Member member, Theme theme, ReservationDate from,
                                                            ReservationDate to);

    @Query(value = """
            SELECT r
            FROM Reservation r
                JOIN FETCH r.theme
                JOIN FETCH r.member
                JOIN FETCH r.reservationTime
            WHERE r.reservationStatus = :reservationStatus
            """)
    List<Reservation> findAllByReservationStatus(ReservationStatus reservationStatus);

    List<Reservation> findAllByDateAndReservationTimeAndThemeAndReservationStatusOrderByIdAsc(
            ReservationDate date,
            ReservationTime reservationTime,
            Theme theme,
            ReservationStatus reservationStatus
    );

    @Query(value = """
            SELECT  new roomescape.domain.reservation.WaitingRankReservation(r, 
                (
                    SELECT COUNT(r2)
                    FROM Reservation r2
                    WHERE 
                        r2.reservationStatus='WAITING' AND 
                        r2.id <= r.id    AND 
                        r2.reservationTime=r.reservationTime AND
                        r2.theme=r.theme AND
                        r2.date=r.date
                ))
            FROM Reservation r
                JOIN FETCH r.theme
                JOIN FETCH r.member
                JOIN FETCH r.reservationTime
            WHERE r.member = :member
                AND r.reservationStatus = 'WAITING'
            ORDER BY r.id ASC
            """)
    List<WaitingRankReservation> findAllByMember(Member member);

    boolean existsByReservationTime(ReservationTime reservationTime);

    boolean existsByTheme(Theme theme);

    boolean existsByDateAndReservationTimeAndThemeAndMember(
            ReservationDate date,
            ReservationTime reservationTime,
            Theme theme,
            Member member
    );

    boolean existsByReservationTimeAndDateAndThemeAndReservationStatus(
            ReservationTime reservationTime, ReservationDate date,
            Theme theme,
            ReservationStatus reservationStatus
    );
}
