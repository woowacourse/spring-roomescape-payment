package roomescape.infra.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;

public interface ReservationDetailJpaRepository extends Repository<ReservationDetail, Long> {

    ReservationDetail save(ReservationDetail reservationDetail);

    Optional<ReservationDetail> findByDateAndTimeAndTheme(LocalDate date, ReservationTime time, Theme theme);

    List<ReservationDetail> findAll();

    void delete(ReservationDetail reservationDetail);
}
