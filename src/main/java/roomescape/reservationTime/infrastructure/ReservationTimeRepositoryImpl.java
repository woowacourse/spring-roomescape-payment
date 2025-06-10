package roomescape.reservationTime.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.ReservationTimeRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationTimeRepositoryImpl implements ReservationTimeRepository {

    private final JpaReservationTimeRepository jpaReservationTimeRepository;

    @Override
    public ReservationTime save(ReservationTime reservationTime) {
        return jpaReservationTimeRepository.save(reservationTime);
    }

    @Override
    public void deleteById(Long id) {
        jpaReservationTimeRepository.deleteById(id);
    }

    @Override
    public Optional<ReservationTime> findById(Long id) {
        return jpaReservationTimeRepository.findById(id);
    }

    @Override
    public List<ReservationTime> findAll() {
        return jpaReservationTimeRepository.findAll();
    }
}
