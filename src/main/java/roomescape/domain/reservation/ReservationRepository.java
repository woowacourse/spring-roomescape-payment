package roomescape.domain.reservation;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByDateAndTimeSlotIdAndThemeIdAndUserId(LocalDate date, Long timeSlotId, Long themeId, Long userId);
}
