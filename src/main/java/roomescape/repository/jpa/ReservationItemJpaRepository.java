package roomescape.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservationItemJpaRepository extends JpaRepository<ReservationItem, Long> {

    Optional<ReservationItem> findReservationItemByDateAndTimeAndTheme(LocalDate date, ReservationTime time, ReservationTheme theme);

    boolean existsByDateAndTimeAndTheme(LocalDate date, ReservationTime time, ReservationTheme theme);
}
