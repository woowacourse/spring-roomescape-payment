package roomescape.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.infrastructure.persistence.jpa.TimeSlotJpaRepository;

@Repository
@RequiredArgsConstructor
public class TimeSlotRepositoryImpl implements TimeSlotRepository {

    private final TimeSlotJpaRepository reservationTimeJpaRepository;

    @Override
    public boolean existsById(final Long id) {
        return reservationTimeJpaRepository.existsById(id);
    }

    @Override
    public TimeSlot save(final TimeSlot timeSlot) {
        return reservationTimeJpaRepository.save(timeSlot);
    }

    @Override
    public void deleteById(final Long id) {
        reservationTimeJpaRepository.deleteById(id);
    }

    @Override
    public List<TimeSlot> findAll() {
        return reservationTimeJpaRepository.findAll();
    }

    @Override
    public Optional<TimeSlot> findById(final Long id) {
        return reservationTimeJpaRepository.findById(id);
    }
}
