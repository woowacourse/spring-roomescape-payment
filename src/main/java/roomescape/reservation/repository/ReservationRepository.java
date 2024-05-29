package roomescape.reservation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByDateAndTimeIdAndThemeId(ReservationDate date, Long timeId, Long themeId);

    boolean existsByTimeId(Long reservationTimeId);

    boolean existsByThemeId(Long themeId);

    List<Reservation> findAllByDateAndThemeId(ReservationDate date, Long themeId);

    List<Reservation> findAllByMember_Id(Long memberId);
}
