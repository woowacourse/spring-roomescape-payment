package roomescape.timeslot.domain;

import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {

    TimeSlot save(TimeSlot timeSlot);

    Optional<TimeSlot> findById(Long id);

    void deleteById(Long id);

    List<TimeSlot> findAll();
}
