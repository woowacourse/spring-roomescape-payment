package roomescape.infra.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;

@Repository
@RequiredArgsConstructor
public class ReservationDetailRepositoryImpl implements ReservationDetailRepository {
    private final ReservationDetailJpaRepository reservationDetailJpaRepository;

    @Override
    public ReservationDetail save(ReservationDetail reservationDetail) {
        return reservationDetailJpaRepository.save(reservationDetail);
    }

    @Override
    public ReservationDetail getReservationDetail(LocalDate date, ReservationTime time, Theme theme) {
        return findReservationDetail(date, time, theme)
                .orElseGet(() -> save(new ReservationDetail(date, time, theme)));
    }

    @Override
    public Optional<ReservationDetail> findReservationDetail(LocalDate date, ReservationTime time, Theme theme) {
        return reservationDetailJpaRepository.findByDateAndTimeAndTheme(date, time, theme);
    }

    @Override
    public List<ReservationDetail> findAll() {
        return reservationDetailJpaRepository.findAll();
    }

    @Override
    public void delete(ReservationDetail reservationDetail) {
        reservationDetailJpaRepository.delete(reservationDetail);
    }
}
