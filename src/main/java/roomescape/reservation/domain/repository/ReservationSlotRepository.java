package roomescape.reservation.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, Long> {
    Optional<ReservationSlot> findFirstByDateAndTimeAndTheme(LocalDate date, ReservationTime time, Theme theme);

    boolean existsByTimeId(long timeId);

    boolean existsByThemeId(long themeId);
}
