package roomescape.domain.timeslot;

import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {

    boolean existsById(Long id);

    TimeSlot save(TimeSlot timeSlot);

    void deleteById(Long id);

    List<TimeSlot> findAll();

    Optional<TimeSlot> findById(Long id);
}
