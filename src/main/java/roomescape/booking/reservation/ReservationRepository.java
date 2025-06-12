package roomescape.booking.reservation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.booking.reservation.dto.ReservationPayment;
import roomescape.reservationtime.ReservationTime;
import roomescape.schedule.Schedule;
import roomescape.theme.Theme;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllBySchedule_ThemeAndSchedule_Date(Theme theme, LocalDate date);

    List<Reservation> findAllByMember_IdAndSchedule_Theme_IdAndSchedule_DateBetween(Long memberId, Long themeId,
                                                                                    LocalDate from, LocalDate to);

    Boolean existsBySchedule_ReservationTime(ReservationTime reservationTime);

    Boolean existsBySchedule_Theme(Theme theme);

    Boolean existsBySchedule(Schedule schedule);

    @Query("""
                SELECT new roomescape.booking.reservation.dto.ReservationPayment(r, p)
                FROM Reservation r
                JOIN r.member m
                LEFT JOIN r.order o
                LEFT JOIN Payment p ON p.order = o
                WHERE m.email = :email
            """)
    List<ReservationPayment> findReservationPaymentsByMember_Email(String email);

    List<Reservation> findAllByMember_Email(String email);
}
