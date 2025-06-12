package roomescape.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import roomescape.domain.timeslot.TimeSlot;

public interface TimeSlotJpaRepository extends CrudRepository<TimeSlot, Long> {

    List<TimeSlot> findAll();
}
