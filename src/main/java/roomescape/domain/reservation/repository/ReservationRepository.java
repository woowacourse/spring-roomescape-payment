package roomescape.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationStatus;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByDateAndTime_IdAndTheme_Id(ReservationDate date, Long time_id, Long theme_id);

    boolean existsByTimeId(Long reservationTimeId);

    boolean existsByThemeId(Long themeId);

    boolean existsByDateAndTime_IdAndTheme_IdAndStatus(ReservationDate date, Long time_id, Long theme_id, ReservationStatus status);

    List<Reservation> findAllByDateAndTheme_Id(ReservationDate date, Long themeId);

    List<Reservation> findAllByMember_Id(Long memberId);
}
