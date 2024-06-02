package roomescape.infra.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.exception.time.NotFoundReservationTimeException;

@Repository
@RequiredArgsConstructor
public class ReservationTimeRepositoryImpl implements ReservationTimeRepository {
    private final ReservationTimeJpaRepository reservationTimeJpaRepository;

    @Override
    public ReservationTime save(ReservationTime time) {
        return reservationTimeJpaRepository.save(time);
    }

    @Override
    public ReservationTime getReservationTime(Long id) {
        return reservationTimeJpaRepository.findById(id)
                .orElseThrow(NotFoundReservationTimeException::new);
    }

    @Override
    public Optional<ReservationTime> findReservationTime(Long id) {
        return reservationTimeJpaRepository.findById(id);
    }

    @Override
    public List<ReservationTime> findAll() {
        return reservationTimeJpaRepository.findAll();
    }

    @Override
    public List<ReservationTime> findAllReservedTime(LocalDate date, Long themeId) {
        return reservationTimeJpaRepository.findAllReservedTimeByDateAndThemeId(date, themeId);
    }

    @Override
    public boolean existsByStartAt(LocalTime startAt) {
        return reservationTimeJpaRepository.existsByStartAt(startAt);
    }

    @Override
    public void delete(Long id) {
        reservationTimeJpaRepository.deleteById(id);
    }
}
