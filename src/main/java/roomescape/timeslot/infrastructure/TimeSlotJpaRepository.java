package roomescape.timeslot.infrastructure;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import roomescape.timeslot.domain.TimeSlot;

public interface TimeSlotJpaRepository extends CrudRepository<TimeSlot, Long> {

    List<TimeSlot> findAll();
}
