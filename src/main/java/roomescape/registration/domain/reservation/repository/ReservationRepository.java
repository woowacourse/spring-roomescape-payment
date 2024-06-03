package roomescape.registration.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.registration.domain.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByOrderByDateAscReservationTimeAsc();

    List<Reservation> findAllByThemeIdAndDate(long themeId, LocalDate date);

    List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(long memberId, long themeId, LocalDate fromDate, LocalDate toDate);

    Reservation findReservationByDateAndThemeIdAndReservationTimeId(LocalDate date, long themeId, long reservationTimeId);

    List<Reservation> findByReservationTimeId(long timeId);

    List<Reservation> findByThemeId(long themeId);

    void deleteById(long reservationId);

    List<Reservation> findAllByMemberId(long id);

    boolean existsByDateAndThemeIdAndReservationTimeIdAndMemberId(LocalDate date, long themeId, long reservationTimeId, long memberId);
}
