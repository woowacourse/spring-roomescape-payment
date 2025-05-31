package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    @EntityGraph(attributePaths = {"theme", "user", "timeSlot"})
    List<Reservation> findByUserId(long id);

    @EntityGraph(attributePaths = {"timeSlot"})
    List<Reservation> findByDateAndThemeId(LocalDate date, long themeId);

    boolean existsByDateAndTimeSlotIdAndThemeId(LocalDate date, long timeSlotId, long themeId);

    boolean existsByTimeSlotId(long timeSlotId);

    boolean existsByThemeId(long themeId);

    boolean existsByDateAndTimeSlotIdAndThemeIdAndUserId(LocalDate date, long timeSlotId, long themeId, long userId);
}
