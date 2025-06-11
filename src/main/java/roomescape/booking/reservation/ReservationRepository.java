package roomescape.booking.reservation;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import roomescape.reservationtime.ReservationTime;
import roomescape.schedule.Schedule;
import roomescape.theme.Theme;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllBySchedule_ThemeAndSchedule_Date(Theme theme, LocalDate date);

    List<Reservation> findAllByMember_IdAndSchedule_Theme_IdAndSchedule_DateBetween(Long memberId, Long themeId, LocalDate from, LocalDate to);

    Boolean existsBySchedule_ReservationTime(ReservationTime reservationTime);

    Boolean existsBySchedule_Theme(Theme theme);

    Boolean existsByScheduleAndReservationStatusNot(Schedule schedule, ReservationStatus reservationStatus);

    Boolean existsBySchedule(Schedule schedule);

    List<Reservation> findAllByMember_Email(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :id")
    Optional<Reservation> findByIdForUpdate(Long id);
}
