package roomescape.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;
import roomescape.repository.jpa.ReservationJpaRepository;
import roomescape.repository.jpa.ReservationTimeJpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReservationTimeRepositoryImpl implements ReservationTimeRepository {

    private final ReservationTimeJpaRepository reservationTimeJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<ReservationTime> findById(final Long id) {
        return reservationTimeJpaRepository.findById(id);
    }

    @Override
    public List<ReservationTime> findAll() {
        return reservationTimeJpaRepository.findAll();
    }

    @Override
    public ReservationTime save(final ReservationTime reservationTime) {
        return reservationTimeJpaRepository.save(reservationTime);
    }

    @Override
    public void deleteById(final long id) {
        reservationTimeJpaRepository.deleteById(id);
    }

    @Override
    public boolean isAvailableToRemove(final long id) {
        return !reservationJpaRepository.existsByReservationItem_Time_Id(id);
    }

    @Override
    public boolean existsById(final long id) {
        return reservationTimeJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByStartAt(final LocalTime startAt) {
        return reservationTimeJpaRepository.existsByStartAt(startAt);
    }
}
