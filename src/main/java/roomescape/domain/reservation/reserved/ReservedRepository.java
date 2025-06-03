package roomescape.domain.reservation.reserved;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReservedRepository extends JpaRepository<Reserved, Long>, JpaSpecificationExecutor<Reserved> {

    @EntityGraph(attributePaths = {"theme", "user", "timeSlot", "payment"})
    List<Reserved> findByUserId(long id);

    @EntityGraph(attributePaths = {"timeSlot"})
    List<Reserved> findByDateAndThemeId(LocalDate date, long themeId);

    boolean existsByDateAndTimeSlotIdAndThemeId(LocalDate date, long timeSlotId, long themeId);

    boolean existsByTimeSlotId(long timeSlotId);

    boolean existsByThemeId(long themeId);

    boolean existsByDateAndTimeSlotIdAndThemeIdAndUserId(LocalDate date, long timeSlotId, long themeId, long userId);
}
