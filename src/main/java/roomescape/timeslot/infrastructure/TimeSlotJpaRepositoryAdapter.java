package roomescape.timeslot.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.timeslot.domain.TimeSlot;
import roomescape.timeslot.domain.TimeSlotRepository;

@Repository
public class TimeSlotJpaRepositoryAdapter implements TimeSlotRepository {

    private final TimeSlotJpaRepository reservationTimeJpaRepository;

    public TimeSlotJpaRepositoryAdapter(TimeSlotJpaRepository reservationTimeJpaRepository) {
        this.reservationTimeJpaRepository = reservationTimeJpaRepository;
    }

    @Override
    public TimeSlot save(TimeSlot timeSlot) {
        return reservationTimeJpaRepository.save(timeSlot);
    }

    @Override
    public Optional<TimeSlot> findById(Long id) {
        return reservationTimeJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        reservationTimeJpaRepository.deleteById(id);
    }

    @Override
    public List<TimeSlot> findAll() {
        return reservationTimeJpaRepository.findAll();
    }
}
